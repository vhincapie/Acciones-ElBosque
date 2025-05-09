package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHConflictException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAccountACHRepository;
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
import java.util.List;

@Service
public class AccountACHService implements IAccountACHService<AccountACHRelationShipDTO> {

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    private static final String API_URL = "https://broker-api.sandbox.alpaca.markets/v1/accounts/{account_id}/ach_relationships";

    private final OkHttpClient client;
    private final IAccountACHRepository achRepository;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public AccountACHService(OkHttpClient client,
                             IAccountACHRepository achRepository,
                             IAlpacaAccountRepository alpacaAccountRepository,
                             UserService userService,
                             ModelMapper modelMapper,
                             ObjectMapper objectMapper) {
        this.client = client;
        this.achRepository = achRepository;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseAccountACHDTO create(AccountACHRelationShipDTO dto) {
        User usuario = userService.getAuthenticatedUserEntity();
        AlpacaAccount alpacaAccount = getAlpacaAccount(usuario);
        String alpacaId = alpacaAccount.getAlpacaId();

        validateCharacters(dto);
        dto.setBankRoutingNumber("123103716");

        ResponseAccountACHDTO responseDTO = executeCreateACHCall(dto, alpacaId);

        AccountACHRelationShip ach = modelMapper.map(responseDTO, AccountACHRelationShip.class);
        ach.setAchId(responseDTO.getId());
        ach.setAlpacaAccount(alpacaAccount);
        ach.setCreatedAt(LocalDateTime.now());

        achRepository.save(ach);
        return responseDTO;
    }

    @Override
    public List<AccountACHRelationShipDTO> getAllForAuthenticatedUser() {
        User usuario = userService.getAuthenticatedUserEntity();
        AlpacaAccount alpacaAccount = getAlpacaAccount(usuario);

        List<AccountACHRelationShip> relaciones = achRepository.findByAlpacaAccount(alpacaAccount);
        return relaciones.stream()
                .map(relacion -> modelMapper.map(relacion, AccountACHRelationShipDTO.class))
                .toList();
    }

    private AlpacaAccount getAlpacaAccount(User usuario) {
        return alpacaAccountRepository.findByUserId(usuario.getId())
                .orElseThrow(() -> new ACHBadRequestException("No se encontro la cuenta Alpaca asociada al usuario."));
    }

    private ResponseAccountACHDTO executeCreateACHCall(AccountACHRelationShipDTO dto, String accountId) {
        try {
            return callAlpacaCreateACHApi(dto, accountId);
        } catch (IOException e) {
            throw new ACHBadRequestException("Error al comunicarse con la API de Alpaca");
        }
    }

    private ResponseAccountACHDTO callAlpacaCreateACHApi(AccountACHRelationShipDTO dto, String accountId) throws IOException {
        String url = API_URL.replace("{account_id}", accountId);
        String json = objectMapper.writeValueAsString(dto);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
        String base64Credentials = Base64.getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes(StandardCharsets.UTF_8));

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

            throw switch (response.code()) {
                case 400 -> new ACHBadRequestException("Solicitud invalida. Verifica los datos enviados.");
                case 409 -> new ACHConflictException("Ya existe una relacion bancaria para esta cuenta.");
                default -> new ACHBadRequestException("Fallo al crear la relación bancaria en Alpaca.");
            };
        }
    }

    private void validateCharacters(AccountACHRelationShipDTO dto) {
        String allowedPattern = "^[A-Za-z0-9 !'()*+,.\\-/:;?=]*$";
        String[] values = {
                dto.getAccountOwnerName(), dto.getBankAccountType(), dto.getBankAccountNumber(),
                dto.getBankRoutingNumber(), dto.getNickname()
        };
        String[] fieldNames = {
                "account_owner_name", "bank_account_type", "bank_account_number",
                "bank_routing_number", "nickname"
        };

        for (int i = 0; i < values.length; i++) {
            if (values[i] != null && !values[i].matches(allowedPattern)) {
                throw new ACHBadRequestException("El campo '" + fieldNames[i] +
                        "' contiene caracteres invalidos. Solo se permiten: A-Z a-z 0-9 y simbolos ! ' ( ) * + , - . / : ; ? = [espacio]");
            }
        }
    }
}
