package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SaldoException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TradingDetailDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ISaldoService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SaldoServiceTest {

    private ISaldoService saldoService;
    private IUserService userService;
    private IAlpacaAccountRepository alpacaAccountRepository;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        alpacaAccountRepository = mock(IAlpacaAccountRepository.class);
        restTemplate = mock(RestTemplate.class);
        saldoService = new SaldoService(userService, alpacaAccountRepository, restTemplate);
        setPrivateField(saldoService, "apiKey", "key");
        setPrivateField(saldoService, "apiSecret", "secret");
        setPrivateField(saldoService, "baseUrl", "http://mocked.url");
    }

    @Test
    void obtenerSaldoUsuarioActualDeberiaRetornarTradingDetailDTO() {
        // Arrange
        User user = new User();
        user.setId(1L);
        AlpacaAccount account = new AlpacaAccount();
        account.setAlpacaId("acc-123");
        TradingDetailDTO detail = new TradingDetailDTO();
        detail.setBuyingPower("1000");

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.of(account));
        when(restTemplate.exchange(eq("http://mocked.url/acc-123/account"), eq(HttpMethod.GET), any(HttpEntity.class), eq(TradingDetailDTO.class)))
                .thenReturn(ResponseEntity.ok(detail));

        // Act
        TradingDetailDTO result = saldoService.obtenerSaldoUsuarioActual();

        // Assert
        assertNotNull(result);
        assertEquals("1000", result.getBuyingPower());
        verify(alpacaAccountRepository).save(any());
    }

    @Test
    void obtenerSaldoUsuarioActualSinCuentaDeberiaLanzarExcepcion() {
        // Arrange
        User user = new User();
        user.setId(1L);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SaldoException.class, () -> saldoService.obtenerSaldoUsuarioActual());
    }

    @Test
    void obtenerSaldoUsuarioActualConFormatoInvalidoDeberiaLanzarExcepcion() {
        // Arrange
        User user = new User();
        user.setId(1L);
        AlpacaAccount account = new AlpacaAccount();
        account.setAlpacaId("acc-123");
        TradingDetailDTO detail = new TradingDetailDTO();
        detail.setBuyingPower("no-numeric");

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.of(account));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(TradingDetailDTO.class)))
                .thenReturn(ResponseEntity.ok(detail));

        // Act & Assert
        assertThrows(SaldoException.class, () -> saldoService.obtenerSaldoUsuarioActual());
    }

    @Test
    void obtenerDetallesCuentaDesdeAlpacaFallaHttpDeberiaLanzarExcepcion() {
        // Arrange
        User user = new User();
        user.setId(1L);
        AlpacaAccount account = new AlpacaAccount();
        account.setAlpacaId("acc-404");

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.of(account));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(TradingDetailDTO.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        // Act & Assert
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(TradingDetailDTO.class))
        ).thenThrow(new HttpClientErrorException(
                HttpStatus.NOT_FOUND,
                "Not Found",
                new byte[0],  // cuerpo vacío, evita el NPE
                null
        ));
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