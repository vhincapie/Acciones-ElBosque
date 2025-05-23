package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.OrderSyncException;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import co.edu.unbosque.accioneselbosqueapi.repository.IOrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class OrderSyncService {

    private final IOrderRepository orderRepository;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    private static final String API_URL = "https://broker-api.sandbox.alpaca.markets/v1/trading/accounts/";
    private static final Logger logger = LoggerFactory.getLogger(OrderSyncService.class);

    public OrderSyncService(IOrderRepository orderRepository, OkHttpClient client, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 10000)
    public void sincronizarEstadosOrdenes() {
        logger.info("Iniciando sincronización de estados de ordenes...");
        List<Order> ordenes = orderRepository.findAll();

        for (Order orden : ordenes) {
            if (!ordenEsValida(orden)) {
                logger.debug("Orden invalida omitida: ID interno {}", orden.getId());
                continue;
            }
            try {
                logger.debug("Sincronizando orden Alpaca ID: {}", orden.getAlpacaOrderId());
                String nuevoEstado = obtenerEstadoActualDesdeAPI(orden);
                actualizarEstadoSiCambio(orden, nuevoEstado);
            } catch (Exception e) {
                logger.error("Error al sincronizar la orden {}: {}", orden.getAlpacaOrderId(), e.getMessage());
                throw new OrderSyncException("Error inesperado al sincronizar la orden " + orden.getAlpacaOrderId(), e);
            }
        }

    }

    private boolean ordenEsValida(Order orden) {
        return orden.getAlpacaOrderId() != null && orden.getAlpacaAccount() != null;
    }

    private String obtenerEstadoActualDesdeAPI(Order orden) {
        String alpacaId = orden.getAlpacaAccount().getAlpacaId();
        String orderId = orden.getAlpacaOrderId();
        String url = API_URL + alpacaId + "/orders/" + orderId;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Basic " + getBasicAuth())
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                logger.warn("Fallo en la respuesta de Alpaca al consultar orden {}. Codigo HTTP: {}", orderId, response.code());
                throw new OrderSyncException("Fallo al consultar la orden " + orderId + " de Alpaca (status HTTP " + response.code() + ")");
            }

            String body = response.body().string();
            JsonNode json = objectMapper.readTree(body);
            String estado = json.get("status").asText();
            logger.debug("Estado actual de la orden {}: {}", orderId, estado);
            return estado;
        } catch (IOException e) {
            logger.error("Error de red al consultar orden {} en Alpaca", orderId, e);
            throw new OrderSyncException("Error de red al sincronizar la orden " + orderId, e);
        }
    }

    private void actualizarEstadoSiCambio(Order orden, String nuevoEstado) {
        if (!orden.getStatus().equalsIgnoreCase(nuevoEstado)) {
            orden.setStatus(nuevoEstado);
            orderRepository.save(orden);
        }
    }

    private String getBasicAuth() {
        String credentials = apiKey + ":" + apiSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
