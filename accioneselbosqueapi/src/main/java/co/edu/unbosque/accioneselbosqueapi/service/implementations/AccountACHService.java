package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHConflictException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.AccountACHRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAccountACHService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AccountACHService implements IAccountACHService<AccountACHRelationShipDTO> {

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    private static final String API_URL = "https://broker-api.sandbox.alpaca.markets/v1/accounts/{account_id}/ach_relationships";

    private final OkHttpClient client;
    private final AccountACHRepository achRepository;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public AccountACHService(OkHttpClient client, AccountACHRepository achRepository, IAlpacaAccountRepository alpacaAccountRepository, UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.client = client;
        this.achRepository = achRepository;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseAccountACHDTO create(AccountACHRelationShipDTO dto) {
        // Obtener el usuario autenticado
        User usuario = userService.getAuthenticatedUserEntity();

        // Buscar la cuenta Alpaca asociada al usuario
        AlpacaAccount alpacaAccount = alpacaAccountRepository.findByUserId(usuario.getId())
                .orElseThrow(() -> new ACHBadRequestException("No se encontró la cuenta Alpaca asociada al usuario."));

        // Obtener el ID generado por Alpaca (UUID tipo String)
        String alpacaId = alpacaAccount.getAlpacaId();

        validateCharacters(dto);

        try {
            // Llamada a la API externa de Alpaca
            ResponseAccountACHDTO responseDTO = callAlpacaCreateACHApi(dto, alpacaId);

            // Mapear y guardar en base de datos
            AccountACHRelationShip ach = modelMapper.map(responseDTO, AccountACHRelationShip.class);
            ach.setAchId(responseDTO.getId());
            ach.setAlpacaAccount(alpacaAccount);
            ach.setCreatedAt(LocalDateTime.now());

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
