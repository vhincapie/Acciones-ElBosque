package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.*;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.MarketStatusDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
import co.edu.unbosque.accioneselbosqueapi.websocket.StockWebSocketHandler;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketService implements IMarketService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private static final String FINNHUB_URL = "https://finnhub.io/api/v1/quote?symbol={symbol}&token=";

    private final StockWebSocketHandler webSocketHandler;
    private final UserService userService;

    @Autowired
    public MarketService(StockWebSocketHandler webSocketHandler, UserService userService) {
        this.webSocketHandler = webSocketHandler;
        this.userService = userService;
    }

    @Override
    public List<StockDTO> getRelevantStocks() {
        User usuario = userService.getAuthenticatedUserEntity();
        String nombreMercado = usuario.getMercadoInteres().getNombre();
        String[] symbols = obtenerSimbolosPorMercado(nombreMercado);
        List<StockDTO> stocks = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        for (String symbol : symbols) {
            try {
                String url = FINNHUB_URL + apiKey;
                String response = restTemplate.getForObject(url, String.class, symbol);

                if (response == null || response.equals("{}")) {
                    throw new SymbolNotFoundException("Sin datos para: " + symbol);
                }

                stocks.add(parseStockResponse(response, symbol));

            } catch (SymbolNotFoundException | MarketDataParseException e) {
                System.out.println("Advertencia: " + e.getMessage() + " [Simbolo: " + symbol + "]");
            } catch (RestClientException e) {
                System.out.println("Error de conexion al consultar el simbolo " + symbol + ": " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado al procesar el simbolo " + symbol + ": " + e.getMessage());
            }
        }

        webSocketHandler.sendStockUpdates(stocks);
        return stocks;
    }

    @Override
    public MarketStatusDTO getMarketStatus() {
        java.time.ZoneId nyZone = java.time.ZoneId.of("America/New_York");
        java.time.ZonedDateTime nowNY = java.time.ZonedDateTime.now(nyZone);

        int hour = nowNY.getHour();
        int minute = nowNY.getMinute();

        boolean abierto = (hour > 9 || (hour == 9 && minute >= 30)) && (hour < 16);

        String mensaje = abierto ? "El mercado esta abierto" : "El mercado esta cerrado";

        return new MarketStatusDTO(abierto, mensaje);
    }

    public StockDTO parseStockResponse(String response, String symbol) {
        try {
            JSONObject jsonResponse = new JSONObject(response);

            double currentPrice = jsonResponse.getDouble("c");
            double highPrice = jsonResponse.getDouble("h");
            double lowPrice = jsonResponse.getDouble("l");
            double previousClosePrice = jsonResponse.getDouble("pc");

            long timestamp = System.currentTimeMillis() / 1000;
            return new StockDTO(symbol, "Descripcion de " + symbol, currentPrice, highPrice, lowPrice, previousClosePrice, timestamp);
        } catch (Exception e) {
            throw new MarketDataParseException("No se pudo procesar la respuesta de Finnhub para " + symbol);
        }
    }

    private String[] obtenerSimbolosPorMercado(String mercado) {
        return switch (mercado.toLowerCase()) {
            case "tecnología" -> new String[]{"AAPL", "MSFT", "GOOG", "NVDA"};
            case "salud" -> new String[]{"JNJ", "PFE", "MRK"};
            case "finanzas" -> new String[]{"JPM", "BAC", "C"};
            case "consumo discrecional" -> new String[]{"HD", "MCD", "NKE"};
            case "consumo básico" -> new String[]{"PG", "KO", "WMT"};
            case "energía" -> new String[]{"XOM", "CVX", "SLB"};
            case "industriales" -> new String[]{"HON", "UNP", "UPS"};
            case "materiales" -> new String[]{"LIN", "APD", "ECL"};
            case "servicios públicos" -> new String[]{"NEE", "DUK", "SO"};
            case "bienes raíces" -> new String[]{"PLD", "AMT", "CCI"};
            case "telecomunicaciones" -> new String[]{"VZ", "T", "TMUS"};
            default -> new String[]{"AAPL"};
        };
    }



}