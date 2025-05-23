package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.TransferSyncException;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer;
import co.edu.unbosque.accioneselbosqueapi.repository.ITransferRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class TransferSyncService {

    private static final Logger logger = LoggerFactory.getLogger(TransferSyncService.class);
    private final ITransferRepository transferRepository;
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    private static final String API_URL = "https://broker-api.sandbox.alpaca.markets/v1/accounts/";

    public TransferSyncService(ITransferRepository transferRepository, OkHttpClient client, ObjectMapper objectMapper) {
        this.transferRepository = transferRepository;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 300000)
    public void sincronizarEstadosTransferencias() {
        logger.info("Iniciando sincronizacion de estados de transferencias...");
        List<Transfer> transferencias = transferRepository.findAll();

        for (Transfer transferencia : transferencias) {
            if (!esTransferenciaValida(transferencia)) {
                logger.debug("Transferencia invalida omitida. ID interna: {}", transferencia.getId());
                continue;
            }
            try {
                logger.debug("Consultando estado para transferencia ID Alpaca: {}", transferencia.getAlpacaTransferId());
                JsonNode listaTransferencias = consultarTransferenciasDesdeAlpaca(transferencia);
                actualizarEstadoSiCambio(transferencia, listaTransferencias);
            } catch (Exception e) {
                logger.error("Error al sincronizar transferencia ID {}: {}", transferencia.getId(), e.getMessage(), e);
                throw new TransferSyncException("Error al sincronizar transferencia ID " + transferencia.getId(), e);
            }
        }
    }

    private boolean esTransferenciaValida(Transfer t) {
        return t.getAlpacaAccount() != null && t.getAlpacaTransferId() != null;
    }

    private JsonNode consultarTransferenciasDesdeAlpaca(Transfer transferencia) throws Exception {
        String alpacaId = transferencia.getAlpacaAccount().getAlpacaId();
        String url = API_URL + alpacaId + "/transfers";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Basic " + getBasicAuth())
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                logger.warn("Fallo al consultar transferencias para cuenta {}. Código HTTP: {}",
                        alpacaId, response.code());
                throw new TransferSyncException("Respuesta invalida al consultar transferencias de Alpaca (HTTP " + response.code() + ")");
            }

            String body = response.body().string();
            logger.debug("Respuesta recibida de Alpaca para cuenta {}: {}", alpacaId, body);
            return objectMapper.readTree(body);
        }
    }

    private void actualizarEstadoSiCambio(Transfer transferencia, JsonNode lista) {
        String transferIdLocal = transferencia.getAlpacaTransferId();

        for (JsonNode json : lista) {
            String transferIdApi = json.get("id").asText();
            if (!transferIdApi.equals(transferIdLocal)) continue;

            String nuevoEstado = json.get("status").asText();
            if (!transferencia.getStatus().equalsIgnoreCase(nuevoEstado)) {
                logger.info("Cambio de estado detectado para transferencia {}: de '{}' a '{}'",
                        transferencia.getId(), transferencia.getStatus(), nuevoEstado);
                transferencia.setStatus(nuevoEstado);
                transferRepository.save(transferencia);
            } else {
                logger.debug("Estado sin cambios para transferencia ID {} (estado actual: {})",
                        transferencia.getId(), transferencia.getStatus());
            }
            break;
        }
    }

    private String getBasicAuth() {
        String credentials = apiKey + ":" + apiSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
