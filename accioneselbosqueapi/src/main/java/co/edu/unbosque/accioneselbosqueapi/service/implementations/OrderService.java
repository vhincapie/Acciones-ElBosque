package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.OrderBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IOrderRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IOrderService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ISaldoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.modelmapper.ModelMapper;
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

    private final OkHttpClient client;
    private final IOrderRepository orderRepository;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final ISaldoService saldoService;
    private final WatchlistService watchlistService;

    public OrderService(OkHttpClient client,
                        IOrderRepository orderRepository,
                        IAlpacaAccountRepository alpacaAccountRepository,
                        UserService userService,
                        ModelMapper modelMapper,
                        ObjectMapper objectMapper,
                        ISaldoService saldoService,
                        WatchlistService watchlistService) {
        this.client = client;
        this.orderRepository = orderRepository;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.saldoService = saldoService;
        this.watchlistService = watchlistService;
    }

    @Override
    public OrderResponseDTO create(OrderRequestDTO orderRequestDTO) {
        var user = userService.getAuthenticatedUserEntity();
        AlpacaAccount alpacaAccount = obtenerCuentaAlpaca(user.getId());
        String accountId = alpacaAccount.getAlpacaId();

        BigDecimal price = obtenerPrecioOrden(orderRequestDTO);
        BigDecimal qty = new BigDecimal(orderRequestDTO.getQty());
        BigDecimal transactionAmount = qty.multiply(price);
        BigDecimal commission = transactionAmount.multiply(COMMISSION_PERCENTAGE);
        BigDecimal netAmount = calcularValorNeto(orderRequestDTO, transactionAmount, commission);

        validarSaldoParaCompra(orderRequestDTO, netAmount);

        OrderResponseDTO responseDTO = callAlpacaCreateOrderApi(orderRequestDTO, accountId);

        Order order = modelMapper.map(responseDTO, Order.class);
        order.setAlpacaAccount(alpacaAccount);
        order.setCreatedAt(LocalDateTime.now());
        order.setAlpacaOrderId(responseDTO.getId());
        order.setTransactionAmount(transactionAmount);
        order.setCommissionAmount(commission);
        order.setNetAmount(netAmount);
        orderRepository.save(order);

        responseDTO.setTransactionAmount(transactionAmount);
        responseDTO.setCommissionAmount(commission);
        responseDTO.setNetAmount(netAmount);

        return responseDTO;
    }

    private AlpacaAccount obtenerCuentaAlpaca(Long userId) {
        return alpacaAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new OrderBadRequestException("No se encontro la cuenta Alpaca del usuario."));
    }

    private BigDecimal obtenerPrecioOrden(OrderRequestDTO dto) {
        return switch (dto.getType().toLowerCase()) {
            case "market" -> {
                var stock = watchlistService.findBySymbol(dto.getSymbol());
                if (stock.getCurrentPrice() <= 0) {
                    throw new OrderBadRequestException("No se pudo obtener el precio actual para " + dto.getSymbol());
                }
                yield BigDecimal.valueOf(stock.getCurrentPrice());
            }
            case "limit", "stop_limit" -> {
                if (dto.getLimitPrice() == null) {
                    throw new OrderBadRequestException("Debe especificar un precio limite.");
                }
                yield new BigDecimal(dto.getLimitPrice());
            }
            case "stop" -> {
                if (dto.getStopPrice() == null) {
                    throw new OrderBadRequestException("Debe especificar un precio de activacion (stop).");
                }
                yield new BigDecimal(dto.getStopPrice());
            }
            default -> throw new OrderBadRequestException("Tipo de orden no soportado: " + dto.getType());
        };
    }

    private BigDecimal calcularValorNeto(OrderRequestDTO dto, BigDecimal transactionAmount, BigDecimal commission) {
        return dto.getSide().equalsIgnoreCase("buy")
                ? transactionAmount.add(commission)
                : transactionAmount.subtract(commission);
    }

    private void validarSaldoParaCompra(OrderRequestDTO dto, BigDecimal netAmount) {
        if (dto.getSide().equalsIgnoreCase("buy")) {
            BigDecimal saldo = saldoService.obtenerSaldoUsuarioActual();
            if (saldo.compareTo(BigDecimal.ZERO) == 0) {
                throw new OrderBadRequestException("No tienes ninguna transferencia acreditada. Por favor transfiere fondos antes de operar.");
            }
            if (netAmount.compareTo(saldo) > 0) {
                throw new OrderBadRequestException("Saldo insuficiente para realizar esta orden. Saldo actual: $" + saldo);
            }
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

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";

                if (response.isSuccessful()) {
                    return objectMapper.readValue(responseBody, OrderResponseDTO.class);
                }

                if (response.code() == 403 && responseBody.contains("insufficient buying power")) {
                    throw new OrderBadRequestException("No tienes suficiente saldo para realizar esta orden.");
                }

                throw new OrderBadRequestException("Error al crear la orden. Código HTTP: " + response.code() + " | Mensaje: " + responseBody);
            }
        } catch (IOException e) {
            throw new OrderBadRequestException("Error al comunicarse con la API de Alpaca");
        }
    }
}
