package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.UsuarioNoEncontradoException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.PortafolioItemDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IOrderRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortafolioServiceTest {

    private PortafolioService portafolioService;
    private IUserService userService;
    private IAlpacaAccountRepository alpacaAccountRepository;
    private IOrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        alpacaAccountRepository = mock(IAlpacaAccountRepository.class);
        orderRepository = mock(IOrderRepository.class);
        portafolioService = new PortafolioService(userService, alpacaAccountRepository, orderRepository);
    }

    @Test
    void getResumenAccionesDebeRetornarActualesYVendidas() {
        // Arrange
        User user = new User();
        user.setId(1L);

        AlpacaAccount account = new AlpacaAccount();
        account.setId(10L);

        Order buyOrder1 = new Order();
        buyOrder1.setSymbol("AAPL");
        buyOrder1.setSide("buy");
        buyOrder1.setQty(BigDecimal.valueOf(10));
        buyOrder1.setTransactionAmount(BigDecimal.valueOf(1500));

        Order buyOrder2 = new Order();
        buyOrder2.setSymbol("AAPL");
        buyOrder2.setSide("buy");
        buyOrder2.setQty(BigDecimal.valueOf(5));
        buyOrder2.setTransactionAmount(BigDecimal.valueOf(750));

        Order sellOrder = new Order();
        sellOrder.setSymbol("AAPL");
        sellOrder.setSide("sell");
        sellOrder.setQty(BigDecimal.valueOf(8));
        sellOrder.setTransactionAmount(BigDecimal.valueOf(1200));

        List<Order> ordenes = List.of(buyOrder1, buyOrder2, sellOrder);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.of(account));
        when(orderRepository.findByAlpacaAccountAndStatus(account, "filled")).thenReturn(ordenes);

        // Act
        Map<String, List<PortafolioItemDTO>> resumen = portafolioService.getResumenAcciones();

        // Assert
        List<PortafolioItemDTO> actuales = resumen.get("actuales");
        List<PortafolioItemDTO> vendidas = resumen.get("vendidas");

        assertEquals(1, actuales.size());
        assertEquals(1, vendidas.size());

        PortafolioItemDTO actual = actuales.get(0);
        assertEquals("AAPL", actual.getSymbol());
        assertEquals(7, actual.getTotalQty());
        assertEquals(new BigDecimal("150.00"), actual.getAverageBuyPrice());

        PortafolioItemDTO vendida = vendidas.get(0);
        assertEquals("AAPL", vendida.getSymbol());
        assertEquals(8, vendida.getTotalVendidas());
        assertEquals(new BigDecimal("150.00"), vendida.getAverageSellPrice());
    }

    @Test
    void getResumenAccionesCuandoCuentaNoExisteDebeLanzarExcepcion() {
        // Arrange
        User user = new User();
        user.setId(1L);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () -> portafolioService.getResumenAcciones());
    }
}
