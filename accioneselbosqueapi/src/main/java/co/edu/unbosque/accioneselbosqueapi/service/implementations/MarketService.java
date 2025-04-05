package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.*;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
import co.edu.unbosque.accioneselbosqueapi.websocket.StockWebSocketHandler;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
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
    public StockDTO findBySymbol(String symbol) {
        if (!isValidUSSymbol(symbol)) {
            throw new SymbolNotFoundException("El simbolo '" + symbol + "' no es valido en el mercado estadounidense.");
        }

        String url = FINNHUB_URL + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(url, String.class, symbol);

            if (response == null || response.equals("{}")) {
                throw new MarketNotFoundException("No se encontraron datos de mercado para el simbolo: " + symbol);
            }

            return parseStockResponse(response, symbol);

        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new MarketServiceUnavailableException("Limite de uso de API alcanzado. Intenta nuevamente mas tarde.");
        } catch (SymbolNotFoundException | MarketNotFoundException e) {
            throw e;
        } catch (RestClientException e) {
            throw new MarketConnectionException("Error de conexion al consultar el simbolo: " + symbol);
        } catch (Exception e) {
            throw new MarketServiceUnavailableException("Error inesperado al consultar el simbolo " + symbol + ": " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 60000)
    @Override
    public List<StockDTO> getRelevantStocks() {
        List<StockDTO> stocks = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        for (String symbol : SYMBOLS) {
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

    private StockDTO parseStockResponse(String response, String symbol) {
        try {

            JSONObject jsonResponse = new JSONObject(response);

            double currentPrice = jsonResponse.getDouble("c");
            double highPrice = jsonResponse.getDouble("h");
            double lowPrice = jsonResponse.getDouble("l");
            double previousClosePrice = jsonResponse.getDouble("pc");

            return new StockDTO(symbol, "Descripcion de " + symbol, currentPrice, highPrice, lowPrice, previousClosePrice);
        } catch (Exception e) {
            throw new MarketDataParseException("No se pudo procesar la respuesta de Finnhub para " + symbol);
        }
    }

    private boolean isValidUSSymbol(String symbol) {
        String url = SYMBOL_LOOKUP_URL + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                if (item.getString("symbol").equalsIgnoreCase(symbol)) {
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            throw new MarketServiceUnavailableException("Error al validar simbolo en mercado de EE.UU.: " + e.getMessage());
        }
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