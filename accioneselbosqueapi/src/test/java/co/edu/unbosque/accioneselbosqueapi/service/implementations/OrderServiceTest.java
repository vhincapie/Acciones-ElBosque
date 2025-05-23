package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.OrderBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TradingDetailDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IOrderRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ISaldoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderService orderService;
    private IOrderRepository orderRepository;
    private IAlpacaAccountRepository alpacaAccountRepository;
    private UserService userService;
    private ISaldoService saldoService;
    private WatchlistService watchlistService;
    private OkHttpClient client;
    private ObjectMapper objectMapper;
    private ModelMapper modelMapper;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(IOrderRepository.class);
        alpacaAccountRepository = mock(IAlpacaAccountRepository.class);
        userService = mock(UserService.class);
        saldoService = mock(ISaldoService.class);
        watchlistService = mock(WatchlistService.class);
        client = mock(OkHttpClient.class);
        objectMapper = new ObjectMapper();
        modelMapper = new ModelMapper();
        emailService = mock(EmailService.class);

        orderService = new OrderService(
                client,
                orderRepository,
                alpacaAccountRepository,
                userService,
                modelMapper,
                objectMapper,
                saldoService,
                watchlistService,
                emailService
        );

        setPrivateField(orderService, "apiKey", "demo");
        setPrivateField(orderService, "apiSecret", "demo");
    }

    @Test
    void crearOrdenMarketConSaldoSuficienteDeberiaFuncionar() throws Exception {
        // Arrange
        OrderRequestDTO request = new OrderRequestDTO();
        request.setSymbol("AAPL");
        request.setQty("2");
        request.setSide("buy");
        request.setType("market");
        request.setTimeInForce("day");

        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");
        user.setNombre("Test User");

        AlpacaAccount account = new AlpacaAccount();
        account.setId(10L);
        account.setAlpacaId("alpaca-123");

        StockDTO stockDTO = new StockDTO();
        stockDTO.setSymbol("AAPL");
        stockDTO.setCurrentPrice(150.00);

        TradingDetailDTO balance = new TradingDetailDTO();
        balance.setBuyingPower("1000");

        OrderResponseDTO simulatedResponse = new OrderResponseDTO();
        simulatedResponse.setId("123");
        simulatedResponse.setSymbol("AAPL");
        simulatedResponse.setQty("2");
        simulatedResponse.setOrderType("market");
        simulatedResponse.setSide("buy");
        simulatedResponse.setStatus("filled");

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.of(account));
        when(watchlistService.findBySymbol("AAPL")).thenReturn(stockDTO);
        when(saldoService.obtenerSaldoUsuarioActual()).thenReturn(balance);

        String jsonResponse = objectMapper.writeValueAsString(simulatedResponse);
        ResponseBody responseBody = ResponseBody.create(jsonResponse, MediaType.parse("application/json"));

        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .request(new Request.Builder().url("http://localhost").build())
                .protocol(Protocol.HTTP_1_1)
                .body(responseBody)
                .build();

        Call call = mock(Call.class);
        when(client.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);

        // Act
        OrderResponseDTO resultado = orderService.create(request);

        // Assert
        assertNotNull(resultado);
        assertEquals("AAPL", resultado.getSymbol());
        assertEquals("2", resultado.getQty());
        assertEquals("filled", resultado.getStatus());

        verify(orderRepository).save(any());
    }

    @Test
    void crearOrdenConSaldoInsuficienteDeberiaLanzarExcepcion() {
        // Arrange
        OrderRequestDTO request = new OrderRequestDTO();
        request.setSymbol("AAPL");
        request.setQty("10");
        request.setSide("buy");
        request.setType("market");
        request.setTimeInForce("day");

        User user = new User();
        user.setId(1L);

        AlpacaAccount account = new AlpacaAccount();
        account.setId(10L);
        account.setAlpacaId("alpaca-123");

        StockDTO stockDTO = new StockDTO();
        stockDTO.setSymbol("AAPL");
        stockDTO.setCurrentPrice(500.00);

        TradingDetailDTO balance = new TradingDetailDTO();
        balance.setBuyingPower("100");

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.of(account));
        when(watchlistService.findBySymbol("AAPL")).thenReturn(stockDTO);
        when(saldoService.obtenerSaldoUsuarioActual()).thenReturn(balance);

        // Act & Assert
        assertThrows(OrderBadRequestException.class, () -> orderService.create(request));
    }

    private void setPrivateField(Object target, String fieldName, String value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo establecer el valor del campo: " + fieldName, e);
        }
    }
}