package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHConflictException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.repository.AccountACHRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAccountACHService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class AccountACHService implements IAccountACHService<AccountACHRelationShipDTO, String> {

    @Value("${alpaca.api-key}")
    private String apiKey;

    @Value("${alpaca.api-secret}")
    private String apiSecret;

    private static final String API_URL = "https://broker-api.sandbox.alpaca.markets/v1/accounts/{account_id}/ach_relationships";

    private final OkHttpClient client;
    private final AccountACHRepository achRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public AccountACHService(OkHttpClient client, AccountACHRepository achRepository, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.client = client;
        this.achRepository = achRepository;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseAccountACHDTO create(AccountACHRelationShipDTO accountACHRelationShipDTO, String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new ACHBadRequestException("El accountId no puede ser nulo o vacío");
        }

        validateCharacters(accountACHRelationShipDTO);

        try {
            ResponseAccountACHDTO responseDTO = callAlpacaCreateACHApi(accountACHRelationShipDTO, accountId);

            AccountACHRelationShip ach = modelMapper.map(responseDTO, AccountACHRelationShip.class);
            ach.setAchId(responseDTO.getId());
            ach.setAccountId(responseDTO.getAccountId());

            achRepository.save(ach);
            return responseDTO;

        } catch (IOException e) {
            throw new ACHBadRequestException("Error al comunicarse con la API de Alpaca");
        }
    }

    private ResponseAccountACHDTO callAlpacaCreateACHApi(AccountACHRelationShipDTO dto, String accountId) throws IOException {
        String url = API_URL.replace("{account_id}", accountId);
        String json = objectMapper.writeValueAsString(dto);

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
                return objectMapper.readValue(responseBody, ResponseAccountACHDTO.class);
            }

            switch (response.code()) {
                case 400 -> throw new ACHBadRequestException("Solicitud inválida. Verifica los datos enviados.");
                case 409 -> throw new ACHConflictException("Ya existe una relación bancaria para esta cuenta.");
                default -> throw new ACHBadRequestException("Fallo al crear la relación bancaria en Alpaca.");
            }
        }
    }

    private void validateCharacters(AccountACHRelationShipDTO dto) {
        String allowedPattern = "^[A-Za-z0-9 !'()*+,.\\-/:;?=]*$";
        String[] fieldsToValidate = {
                dto.getAccountOwnerName(), dto.getBankAccountType(), dto.getBankAccountNumber(),
                dto.getBankRoutingNumber(), dto.getNickname()
        };
        String[] fieldNames = {
                "account_owner_name", "bank_account_type", "bank_account_number", "bank_routing_number",
                "nickname"
        };

        for (int i = 0; i < fieldsToValidate.length; i++) {
            String value = fieldsToValidate[i];
            if (value != null && !value.matches(allowedPattern)) {
                throw new ACHBadRequestException("El campo '" + fieldNames[i] +
                        "' contiene caracteres inválidos. Solo se permiten: A-Z a-z 0-9 y símbolos ! ' ( ) * + , - . / : ; ? = [espacio]");
            }
        }
    }
}
