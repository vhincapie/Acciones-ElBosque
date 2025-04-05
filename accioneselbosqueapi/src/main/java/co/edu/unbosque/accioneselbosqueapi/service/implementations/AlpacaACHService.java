package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseACHDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.ACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.repositories.ACHRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAlpacaACHService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AlpacaACHService implements IAlpacaACHService<ACHRelationShipDTO, Integer> {

    @Value("${alpaca.api-key}")
    private String apiKey;

    @Value("${alpaca.api-secret}")
    private String apiSecret;

    private static final String API_URL = "https://broker-api.sandbox.alpaca.markets/v1/accounts/{account_id}/recipient_banks";

    private final OkHttpClient client;
    private final ACHRepository achRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public AlpacaACHService(ACHRepository achRepository, ModelMapper modelMapper, ObjectMapper objectMapper, OkHttpClient client) {
        this.achRepository = achRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
        this.client = client;
    }

    @Override
    public ResponseACHDTO create(ACHRelationShipDTO achRelationShipDTO, String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("El accountId no puede ser nulo o vacío");
        }

        // Validación condicional si es internacional (BIC)
        if ("BIC".equalsIgnoreCase(achRelationShipDTO.getBankCodeType())) {
            if (isNullOrEmpty(achRelationShipDTO.getCountry()) ||
                    isNullOrEmpty(achRelationShipDTO.getStateProvince()) ||
                    isNullOrEmpty(achRelationShipDTO.getPostalCode()) ||
                    achRelationShipDTO.getPostalCode().length() < 3 ||
                    isNullOrEmpty(achRelationShipDTO.getCity()) ||
                    isNullOrEmpty(achRelationShipDTO.getStreetAddress())) {

                throw new IllegalArgumentException("❌ Los campos de dirección internacional son requeridos cuando el bank_code_type es BIC.");
            }
        }

        String url = API_URL.replace("{account_id}", accountId);

        try {
            String json = objectMapper.writeValueAsString(achRelationShipDTO);
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
                if (response.body() == null) {
                    throw new RuntimeException("Respuesta vacía desde Alpaca.");
                }

                String responseBody = response.body().string();

                if (response.isSuccessful()) {
                    ResponseACHDTO responseDTO = objectMapper.readValue(responseBody, ResponseACHDTO.class);

                    ACHRelationShip achRelationShip = modelMapper.map(responseDTO, ACHRelationShip.class);
                    achRelationShip.setAchId(responseDTO.getId());

                    achRepository.save(achRelationShip);

                    System.out.println("✅ Cuenta bancaria creada exitosamente con ID: " + responseDTO.getId());
                    return responseDTO;

                } else {
                    System.err.println("❌ Error HTTP " + response.code());
                    System.err.println("🔹 Respuesta de error: " + responseBody);
                    throw new RuntimeException("Fallo al crear la relación bancaria en Alpaca");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("❌ Error al comunicarse con Alpaca API", e);
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

}
