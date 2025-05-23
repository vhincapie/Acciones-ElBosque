package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseTransferDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TransferDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAccountACHRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.ITransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransferServiceTest {

    private TransferService transferService;
    private ITransferRepository transferRepository;
    private IAccountACHRepository achRepository;
    private IAlpacaAccountRepository alpacaAccountRepository;
    private UserService userService;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;
    private OkHttpClient client;

    @BeforeEach
    void setUp() {
        transferRepository = mock(ITransferRepository.class);
        achRepository = mock(IAccountACHRepository.class);
        alpacaAccountRepository = mock(IAlpacaAccountRepository.class);
        userService = mock(UserService.class);
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
        client = mock(OkHttpClient.class);

        transferService = new TransferService(
                client,
                transferRepository,
                achRepository,
                alpacaAccountRepository,
                userService,
                modelMapper,
                objectMapper
        );

        setPrivateField(transferService, "apiKey", "demo");
        setPrivateField(transferService, "apiSecret", "demo");

        modelMapper.createTypeMap(ResponseTransferDTO.class, co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer.class)
                .addMappings(mapper -> {
                    mapper.skip(co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer::setId);
                    mapper.map(ResponseTransferDTO::getId, co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer::setAlpacaTransferId);
                    mapper.map(ResponseTransferDTO::getType, co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer::setType);
                    mapper.map(src -> src.getAmount() != null ? new BigDecimal(src.getAmount()) : null, co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer::setAmount);
                    mapper.map(ResponseTransferDTO::getCurrency, co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer::setCurrency);
                    mapper.map(ResponseTransferDTO::getStatus, co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer::setStatus);
                });
    }

    @Test
    void crearTransferenciaExitosaDeberiaRetornarResponseDTO() throws Exception {
        // Arrange
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(new BigDecimal("150.00"));

        User user = new User();
        user.setId(1L);

        AlpacaAccount alpaca = new AlpacaAccount();
        alpaca.setId(1L);
        alpaca.setAlpacaId("alpaca-abc");

        AccountACHRelationShip ach = new AccountACHRelationShip();
        ach.setAchId("rel-001");

        ResponseTransferDTO responseMock = new ResponseTransferDTO();
        responseMock.setId("transfer-123");
        responseMock.setAmount("150.00");
        responseMock.setStatus("PENDING");

        String jsonResponse = objectMapper.writeValueAsString(responseMock);
        ResponseBody responseBody = ResponseBody.create(jsonResponse, MediaType.get("application/json"));
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .request(new Request.Builder().url("http://localhost").build())
                .protocol(Protocol.HTTP_1_1)
                .body(responseBody)
                .build();

        Call call = mock(Call.class);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.of(alpaca));
        when(achRepository.findFirstByAlpacaAccount(alpaca)).thenReturn(Optional.of(ach));
        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);

        // Act
        ResponseTransferDTO result = transferService.create(transferDTO);

        // Assert
        assertNotNull(result);
        assertEquals("transfer-123", result.getId());
        assertEquals("150.00", result.getAmount());
        assertEquals("PENDING", result.getStatus());
        verify(transferRepository).save(any());
    }

    private void setPrivateField(Object target, String fieldName, String value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}