package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.OrderBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.OrderRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IOrderService;
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
    private final OrderRepository orderRepository;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public OrderService(OkHttpClient client, OrderRepository orderRepository, IAlpacaAccountRepository alpacaAccountRepository, UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.client = client;
        this.orderRepository = orderRepository;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public OrderResponseDTO create(OrderRequestDTO orderRequestDTO) {
        // Obtener cuenta del usuario autenticado
        var user = userService.getAuthenticatedUserEntity();
        AlpacaAccount alpacaAccount = alpacaAccountRepository.findByUser(user)
                .orElseThrow(() -> new OrderBadRequestException("No se encontró la cuenta Alpaca del usuario."));

        String accountId = alpacaAccount.getAlpacaId();

        OrderResponseDTO responseDTO = callAlpacaCreateOrderApi(orderRequestDTO, accountId);
        Order order = modelMapper.map(responseDTO, Order.class);
        order.setAlpacaAccount(alpacaAccount);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal qty = new BigDecimal(responseDTO.getQty());
        BigDecimal price = new BigDecimal(responseDTO.getLimitPrice() != null ? responseDTO.getLimitPrice() : "0");
        BigDecimal transactionAmount = qty.multiply(price);
        BigDecimal commission = transactionAmount.multiply(COMMISSION_PERCENTAGE);
        BigDecimal netAmount = responseDTO.getSide().equalsIgnoreCase("buy")
                ? transactionAmount.add(commission)
                : transactionAmount.subtract(commission);

        order.setTransactionAmount(transactionAmount);
        order.setCommissionAmount(commission);
        order.setNetAmount(netAmount);

        orderRepository.save(order);

        responseDTO.setTransactionAmount(transactionAmount);
        responseDTO.setCommissionAmount(commission);
        responseDTO.setNetAmount(netAmount);

        return responseDTO;
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
                } else {
                    if (response.code() == 403 && responseBody.contains("insufficient buying power")) {
                        throw new OrderBadRequestException("No tienes suficiente saldo para realizar esta orden.");
                    }
                    throw new OrderBadRequestException("Error al crear la orden. Código HTTP: " + response.code() + " | Mensaje: " + responseBody);
                }
            }
        } catch (IOException e) {
            throw new OrderBadRequestException("Error al comunicarse con la API de Alpaca");
        }
    }
}
