package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.TransferBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseTransferDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TransferDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.AccountACHRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.TransferRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ITransferService;
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
public class TransferService implements ITransferService<TransferDTO> {

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    private static final String API_URL = "https://broker-api.sandbox.alpaca.markets/v1/accounts/{account_id}/transfers";

    private final OkHttpClient client;
    private final TransferRepository transferRepository;
    private final AccountACHRepository accountACHRepository;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public TransferService(OkHttpClient client, TransferRepository transferRepository, AccountACHRepository accountACHRepository, IAlpacaAccountRepository alpacaAccountRepository, UserService userService, ModelMapper modelMapper, ObjectMapper objectMapper) {
        this.client = client;
        this.transferRepository = transferRepository;
        this.accountACHRepository = accountACHRepository;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseTransferDTO create(TransferDTO transferDTO) {
        // Obtener usuario autenticado y su cuenta Alpaca
        User user = userService.getAuthenticatedUserEntity();

        AlpacaAccount alpacaAccount = alpacaAccountRepository.findByUser(user)
                .orElseThrow(() -> new TransferBadRequestException("No se encontró la cuenta Alpaca del usuario autenticado."));

        String alpacaId = alpacaAccount.getAlpacaId();

        // Obtener la relación ACH asociada a la cuenta
        AccountACHRelationShip achRelation = accountACHRepository.findFirstByAlpacaAccount(alpacaAccount)
                .orElseThrow(() -> new TransferBadRequestException("No se encontró una relación bancaria asociada a la cuenta Alpaca."));

        // Preparar el cuerpo con los datos necesarios
        transferDTO.setRelationshipId(achRelation.getAchId());

        try {
            // Llamar a Alpaca con el accountId real
            ResponseTransferDTO responseDTO = callAlpacaCreateTransferApi(transferDTO, alpacaId);

            Transfer transfer = modelMapper.map(responseDTO, Transfer.class);
            transfer.setAlpacaAccount(alpacaAccount);
            transfer.setAccountACHRelationShip(achRelation);
            transfer.setCreatedAt(LocalDateTime.now());

            transferRepository.save(transfer);
            return responseDTO;

        } catch (IOException e) {
            throw new TransferBadRequestException("Error al comunicarse con la API de Alpaca.");
        }
    }

    private ResponseTransferDTO callAlpacaCreateTransferApi(TransferDTO transferDTO, String accountId) throws IOException {
        String url = API_URL.replace("{account_id}", accountId);
        String json = objectMapper.writeValueAsString(transferDTO);

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
                return objectMapper.readValue(responseBody, ResponseTransferDTO.class);
            }

            throw new TransferBadRequestException("Error al crear la transferencia. Código HTTP: "
                    + response.code() + " | Mensaje: " + responseBody);
        }
    }
}
