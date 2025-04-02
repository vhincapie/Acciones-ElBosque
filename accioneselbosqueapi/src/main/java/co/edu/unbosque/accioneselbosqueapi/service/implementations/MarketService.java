package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
import co.edu.unbosque.accioneselbosqueapi.websocket.StockWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketService implements IMarketService {

    @Value("${finnhub.api.key}")
    private String apiKey;
    private static final String FINNHUB_URL = "https://finnhub.io/api/v1/quote?symbol={symbol}&token=";
    private static final String SYMBOL_LOOKUP_URL = "https://finnhub.io/api/v1/stock/symbol?exchange=US&token=";
    private static final String SYMBOLS[] = {"AAPL", "TSLA", "GOOG", "AMZN", "MSFT"};

    private final StockWebSocketHandler webSocketHandler;

    @Autowired
    public MarketService(StockWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }


    @Override
    public String findBySymbol(String symbol) {
        String url = FINNHUB_URL + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        try {
            String response = restTemplate.getForObject(url, String.class, symbol);
            System.out.println("Respuesta de Finnhub: " + response);

            if (response == null || response.contains("error")) {
                return "Datos no disponibles para este símbolo";
            }
            return response;
        } catch (Exception e) {
            return "Error al obtener los datos: " + e.getMessage();
        }
    }

    @Scheduled(fixedRate = 10000)
    @Override
    public List<StockDTO> getRelevantStocks() {
        RestTemplate restTemplate = new RestTemplate();
        List<StockDTO> stocks = new ArrayList<>();
        for (String symbol : SYMBOLS) {
            String url = FINNHUB_URL + apiKey;
            try {
                String response = restTemplate.getForObject(url, String.class, symbol);
                if (response != null) {
                    StockDTO stock = parseStockResponse(response, symbol);
                    stocks.add(stock);
                }
            } catch (Exception e) {
                System.out.println("Error al obtener los datos de " + symbol + ": " + e.getMessage());
            }
        }
        webSocketHandler.sendStockUpdates(stocks);
        return stocks;
    }

    private StockDTO parseStockResponse(String response, String symbol) {
        JSONObject jsonResponse = new JSONObject(response);

        double currentPrice = jsonResponse.getDouble("c");
        double highPrice = jsonResponse.getDouble("h");
        double lowPrice = jsonResponse.getDouble("l");
        double previousClosePrice = jsonResponse.getDouble("pc");

        return new StockDTO(symbol, "Descripción de " + symbol, currentPrice, highPrice, lowPrice, previousClosePrice);
    }


  /*  @Override
    public String findAllSymbols() {
        String urlTemplate = SYMBOL_LOOKUP_URL + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder allSymbols = new StringBuilder();

        try {
            String response = restTemplate.getForObject(urlTemplate, String.class);

            if (response != null && !response.contains("error")) {
                allSymbols.append(response);
            } else {
                allSymbols.append("No se encontraron resultados para las acciones de EE. UU.\n");
            }
            return allSymbols.toString();
        } catch (Exception e) {
            return "Error al obtener los símbolos: " + e.getMessage();
        }
    }*/
}