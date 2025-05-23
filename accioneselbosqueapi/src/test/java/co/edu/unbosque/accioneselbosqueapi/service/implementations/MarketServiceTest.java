package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.MarketInterest;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MarketServiceTest {

    private IUserService userService;
    private RestTemplate restTemplate;
    private IMarketService service;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        restTemplate = mock(RestTemplate.class);
        service = new MarketService(userService, restTemplate);
        ReflectionTestUtils.setField(service, "apiKey", "fake_api_key");
    }

    @Test
    void getRelevantStocksReturnsStockList() {
        // Arrange
        User mockUser = new User();
        MarketInterest marketInterest = new MarketInterest();
        marketInterest.setNombre("tecnología");
        mockUser.setMercadoInteres(marketInterest);
        when(userService.getAuthenticatedUserEntity()).thenReturn(mockUser);

        String mockResponse = """
            {
                "c": 150.0,
                "h": 155.0,
                "l": 148.0,
                "pc": 149.5
            }
        """;


        for (String symbol : new String[]{"AAPL", "MSFT", "GOOG", "NVDA"}) {
            when(restTemplate.getForObject(
                    eq("https://finnhub.io/api/v1/quote?symbol={symbol}&token=fake_api_key"),
                    eq(String.class),
                    eq(symbol)
            )).thenReturn(mockResponse);
        }

        // Act
        List<StockDTO> result = service.getRelevantStocks();

        // Assert
        assertNotNull(result);
        assertEquals(4, result.size());
        for (StockDTO dto : result) {
            assertEquals(150.0, dto.getCurrentPrice());
            assertEquals(155.0, dto.getHighPrice());
            assertEquals(148.0, dto.getLowPrice());
            assertEquals(149.5, dto.getPreviousClosePrice());
            assertNotNull(dto.getSymbol());
        }
    }
}
