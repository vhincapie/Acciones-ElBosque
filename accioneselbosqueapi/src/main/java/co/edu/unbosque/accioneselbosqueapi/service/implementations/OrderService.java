package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.OrderBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TradingDetailDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IOrderRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IOrderService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ISaldoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class OrderService implements IOrderService<OrderRequestDTO> {

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    private static final String API_URL = "https://broker-api.sandbox.alpaca.markets/v1/trading/accounts/{account_id}/orders";
    private static final BigDecimal COMMISSION_PERCENTAGE = new BigDecimal("0.02");
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OkHttpClient client;
    private final IOrderRepository orderRepository;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final ISaldoService saldoService;
    private final WatchlistService watchlistService;
    private final EmailService emailService;

    public OrderService(OkHttpClient client,
                        IOrderRepository orderRepository,
                        IAlpacaAccountRepository alpacaAccountRepository,
                        UserService userService,
                        ModelMapper modelMapper,
                        ObjectMapper objectMapper,
                        ISaldoService saldoService,
                        WatchlistService watchlistService,
                        EmailService emailService) {
        this.client = client;
        this.orderRepository = orderRepository;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.saldoService = saldoService;
        this.watchlistService = watchlistService;
        this.emailService = emailService;
    }

    @Override
    public OrderResponseDTO create(OrderRequestDTO orderRequestDTO) {
        logger.info("Iniciando creacion de orden para el usuario autenticado");

        var user = userService.getAuthenticatedUserEntity();
        AlpacaAccount alpacaAccount = obtenerCuentaAlpaca(user.getId());
        String accountId = alpacaAccount.getAlpacaId();
        logger.info("Cuenta Alpaca obtenida: {}", accountId);

        BigDecimal price = obtenerPrecioOrden(orderRequestDTO);
        BigDecimal qty = new BigDecimal(orderRequestDTO.getQty());
        BigDecimal transactionAmount = qty.multiply(price);
        BigDecimal commission = transactionAmount.multiply(COMMISSION_PERCENTAGE);
        BigDecimal netAmount = calcularValorNeto(orderRequestDTO, transactionAmount, commission);
        logger.info("Monto transaccion: {}, Comision: {}, Neto: {}", transactionAmount, commission, netAmount);

        validarSaldoParaCompra(orderRequestDTO, netAmount);
        logger.info("Saldo validado correctamente");

        OrderResponseDTO responseDTO = callAlpacaCreateOrderApi(orderRequestDTO, accountId);
        logger.info("Orden enviada a Alpaca exitosamente. ID: {}", responseDTO.getId());

        Order order = modelMapper.map(responseDTO, Order.class);
        order.setAlpacaAccount(alpacaAccount);
        order.setCreatedAt(LocalDateTime.now());
        order.setAlpacaOrderId(responseDTO.getId());
        order.setTransactionAmount(transactionAmount);
        order.setCommissionAmount(commission);
        order.setNetAmount(netAmount);
        orderRepository.save(order);
        logger.info("Orden persistida con ID interno: {}", order.getId());

        actualizarSaldoDesdeAlpaca(alpacaAccount);

        responseDTO.setTransactionAmount(transactionAmount);
        responseDTO.setCommissionAmount(commission);
        responseDTO.setNetAmount(netAmount);

        String asunto = "Orden de " + orderRequestDTO.getSide().toUpperCase() + " ejecutada";
        String cuerpo = generarMensajeOrden(orderRequestDTO, user.getNombre(), price, commission, netAmount);
        emailService.enviarCorreo(user.getEmail(), asunto, cuerpo);
        logger.info("Correo de notificacion enviado a {}", user.getEmail());

        return responseDTO;
    }

    private AlpacaAccount obtenerCuentaAlpaca(Long userId) {
        return alpacaAccountRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("No se encontro cuenta Alpaca para usuario ID {}", userId);
                    return new OrderBadRequestException("No se encontro la cuenta Alpaca del usuario.");
                });
    }

    private BigDecimal obtenerPrecioOrden(OrderRequestDTO dto) {
        return switch (dto.getType().toLowerCase()) {
            case "market" -> {
                var stock = watchlistService.findBySymbol(dto.getSymbol());
                if (stock.getCurrentPrice() <= 0) {
                    logger.warn("Precio actual invalido para simbolo {}", dto.getSymbol());
                    throw new OrderBadRequestException("No se pudo obtener el precio actual para " + dto.getSymbol());
                }
                yield BigDecimal.valueOf(stock.getCurrentPrice());
            }
            case "limit", "stop_limit" -> {
                if (dto.getLimitPrice() == null) {
                    logger.warn("Precio limite no especificado para orden limit/stop_limit");
                    throw new OrderBadRequestException("Debe especificar un precio limite.");
                }
                yield new BigDecimal(dto.getLimitPrice());
            }
            case "stop" -> {
                if (dto.getStopPrice() == null) {
                    logger.warn("Precio de activacion (stop) no especificado para orden stop");
                    throw new OrderBadRequestException("Debe especificar un precio de activacion (stop).");
                }
                yield new BigDecimal(dto.getStopPrice());
            }
            default -> {
                logger.error("Tipo de orden no soportado: {}", dto.getType());
                throw new OrderBadRequestException("Tipo de orden no soportado: " + dto.getType());
            }
        };
    }

    private BigDecimal calcularValorNeto(OrderRequestDTO dto, BigDecimal transactionAmount, BigDecimal commission) {
        return dto.getSide().equalsIgnoreCase("buy")
                ? transactionAmount.add(commission)
                : transactionAmount.subtract(commission);
    }

    private void validarSaldoParaCompra(OrderRequestDTO dto, BigDecimal netAmount) {
        if (dto.getSide().equalsIgnoreCase("buy")) {
            BigDecimal saldo = new BigDecimal(saldoService.obtenerSaldoUsuarioActual().getBuyingPower());
            if (saldo.compareTo(BigDecimal.ZERO) == 0) {
                logger.warn("Usuario sin saldo acreditado");
                throw new OrderBadRequestException("No tienes ninguna transferencia acreditada. Por favor transfiere fondos antes de operar.");
            }
            if (netAmount.compareTo(saldo) > 0) {
                logger.warn("Saldo insuficiente. Requiere: {}, Disponible: {}", netAmount, saldo);
                throw new OrderBadRequestException("Saldo insuficiente para realizar esta orden. Saldo actual: $" + saldo);
            }
        }
    }

    private void actualizarSaldoDesdeAlpaca(AlpacaAccount alpacaAccount) {
        try {
            TradingDetailDTO saldoActualizado = saldoService.obtenerSaldoUsuarioActual();
            BigDecimal nuevoSaldo = new BigDecimal(saldoActualizado.getBuyingPower());
            alpacaAccount.setBuyingPower(nuevoSaldo);
            alpacaAccountRepository.save(alpacaAccount);
            logger.info("Saldo actualizado en AlpacaAccount: {}", nuevoSaldo);
        } catch (NumberFormatException e) {
            logger.warn("No se pudo convertir el buying power recibido: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error al actualizar el saldo desde Alpaca luego de la orden", e);
        }
    }

    private OrderResponseDTO callAlpacaCreateOrderApi(OrderRequestDTO orderRequestDTO, String accountId) {
        try {
            String url = API_URL.replace("{account_id}", accountId);
            String json = objectMapper.writeValueAsString(orderRequestDTO);

            RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
            String credentials = apiKey + ":" + apiSecret;
            String base64Credentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic " + base64Credentials)
                    .build();

            logger.debug("Llamando a Alpaca API con URL: {}", url);

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    logger.debug("Respuesta exitosa de Alpaca: {}", responseBody);
                    return objectMapper.readValue(responseBody, OrderResponseDTO.class);
                }
                logger.error("Fallo Alpaca - Código: {}, Respuesta: {}", response.code(), responseBody);

                if (response.code() == 403 && responseBody.contains("insufficient buying power")) {
                    throw new OrderBadRequestException("No tienes suficiente saldo para realizar esta orden.");
                }

                throw new OrderBadRequestException("Error al crear la orden. Código HTTP: " + response.code() + " | Mensaje: " + responseBody);
            }
        } catch (IOException e) {
            logger.error("Excepcion al comunicarse con la API de Alpaca", e);
            throw new OrderBadRequestException("Error al comunicarse con la API de Alpaca");
        }
    }

    private String generarMensajeOrden(OrderRequestDTO request, String nombreUsuario,
                                       BigDecimal price, BigDecimal commission, BigDecimal netAmount) {
        return "Hola " + nombreUsuario + ",\n\n" +
                "Se ha creado una orden de tipo: *" + request.getType().toUpperCase() + "*\n" +
                "Acción: " + request.getSymbol() + "\n" +
                "Cantidad: " + request.getQty() + "\n" +
                "Precio estimado: $" + price + "\n" +
                "Comisión aplicada: $" + commission + "\n" +
                "Monto total neto: $" + netAmount + "\n\n" +
                "Fecha: " + LocalDateTime.now() + "\n\n" +
                "Gracias por operar con Acciones El Bosque.";
    }
}
