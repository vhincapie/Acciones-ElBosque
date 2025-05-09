package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.OrderSyncException;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import co.edu.unbosque.accioneselbosqueapi.repository.IOrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
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

    public OrderSyncService(IOrderRepository orderRepository, OkHttpClient client, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 10000)
    public void sincronizarEstadosOrdenes() {
        List<Order> ordenes = orderRepository.findAll();

        for (Order orden : ordenes) {
            if (!ordenEsValida(orden)) continue;

            try {
                String nuevoEstado = obtenerEstadoActualDesdeAPI(orden);
                actualizarEstadoSiCambio(orden, nuevoEstado);
            } catch (Exception e) {
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
                throw new OrderSyncException("Fallo al consultar la orden " + orderId + " de Alpaca (status HTTP " + response.code() + ")");
            }

            JsonNode json = objectMapper.readTree(response.body().string());
            return json.get("status").asText();

        } catch (IOException e) {
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
