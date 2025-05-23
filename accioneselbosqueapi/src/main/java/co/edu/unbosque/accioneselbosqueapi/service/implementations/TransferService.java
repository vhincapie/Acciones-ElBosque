package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.TransferBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.UsuarioNoEncontradoException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseTransferDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TransferDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAccountACHRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.ITransferRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ITransferService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);
    private final OkHttpClient client;
    private final ITransferRepository transferRepository;
    private final IAccountACHRepository achRepository;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public TransferService(OkHttpClient client,
                           ITransferRepository transferRepository,
                           IAccountACHRepository achRepository,
                           IAlpacaAccountRepository alpacaAccountRepository,
                           IUserService userService,
                           ModelMapper modelMapper,
                           ObjectMapper objectMapper) {
        this.client = client;
        this.transferRepository = transferRepository;
        this.achRepository = achRepository;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseTransferDTO create(TransferDTO transferDTO) {
        User user = userService.getAuthenticatedUserEntity();
        logger.info("Iniciando creacion de transferencia para usuario ID: {}", user.getId());

        AlpacaAccount alpacaAccount = obtenerCuentaAlpaca(user.getId());
        logger.debug("Cuenta Alpaca obtenida: {}", alpacaAccount.getAlpacaId());

        AccountACHRelationShip achRelation = obtenerRelacionACH(alpacaAccount);
        logger.debug("Relacion ACH obtenida: {}", achRelation.getAchId());

        prepararTransferencia(transferDTO, achRelation);
        logger.debug("Transferencia preparada con relationshipId: {}", transferDTO.getRelationshipId());

        ResponseTransferDTO responseDTO = ejecutarTransferencia(transferDTO, alpacaAccount.getAlpacaId());
        logger.info("Transferencia creada exitosamente con ID Alpaca: {}", responseDTO.getId());

        Transfer transferencia = modelMapper.map(responseDTO, Transfer.class);
        transferencia.setAlpacaAccount(alpacaAccount);
        transferencia.setAccountACHRelationShip(achRelation);
        transferencia.setCreatedAt(LocalDateTime.now());
        transferencia.setAlpacaTransferId(responseDTO.getId());

        transferRepository.save(transferencia);
        logger.info("Transferencia persistida en base de datos con ID interno: {}", transferencia.getId());

        return responseDTO;
    }

    private AlpacaAccount obtenerCuentaAlpaca(Long userId) {
        return alpacaAccountRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("No se encontro cuenta Alpaca para el usuario ID: {}", userId);
                    return new UsuarioNoEncontradoException("No se encontro la cuenta Alpaca del usuario autenticado.");
                });
    }

    private AccountACHRelationShip obtenerRelacionACH(AlpacaAccount alpacaAccount) {
        return achRepository.findFirstByAlpacaAccount(alpacaAccount)
                .orElseThrow(() -> {
                    logger.error("No se encontro relación ACH para la cuenta Alpaca: {}", alpacaAccount.getAlpacaId());
                    return new TransferBadRequestException("No se encontro una relacion bancaria asociada a la cuenta Alpaca.");
                });
    }

    private void prepararTransferencia(TransferDTO transferDTO, AccountACHRelationShip achRelation) {
        transferDTO.setRelationshipId(achRelation.getAchId());
        transferDTO.setTransferType("ach");
        transferDTO.setDirection("INCOMING");
        transferDTO.setTiming("immediate");
    }

    private ResponseTransferDTO ejecutarTransferencia(TransferDTO transferDTO, String accountId) {
        try {
            return callAlpacaCreateTransferApi(transferDTO, accountId);
        } catch (IOException e) {
            logger.error("Error de comunicacion con la API de Alpaca al crear transferencia", e);
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

        logger.debug("Enviando solicitud de transferencia a Alpaca API: {}", url);

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (response.isSuccessful()) {
                logger.debug("Respuesta exitosa de Alpaca: {}", responseBody);
                return objectMapper.readValue(responseBody, ResponseTransferDTO.class);
            }

            logger.warn("Error en respuesta de Alpaca al crear transferencia. Código: {}, Mensaje: {}", response.code(), responseBody);

            if (responseBody.contains("maximum number of ACH transfers allowed is 1 per trading day in each direction")) {
                throw new TransferBadRequestException("Ya realizaste una transferencia ACH de este tipo hoy. Solo se permite una por dia habil.");
            }

            throw new TransferBadRequestException("Error al crear la transferencia. Código HTTP: "
                    + response.code() + " | Mensaje: " + responseBody);
        }
    }
}
