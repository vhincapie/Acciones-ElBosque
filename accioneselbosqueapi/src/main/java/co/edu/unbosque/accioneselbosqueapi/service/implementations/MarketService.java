package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ExternalServiceException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.MarketDataParseException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SymbolNotFoundException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.MarketStatusDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketService implements IMarketService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private static final String FINNHUB_URL = "https://finnhub.io/api/v1/quote?symbol={symbol}&token=";

    private final UserService userService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public MarketService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public List<StockDTO> getRelevantStocks() {
        User usuario = userService.getAuthenticatedUserEntity();
        String nombreMercado = usuario.getMercadoInteres().getNombre();
        String[] symbols = resolverSimbolos(nombreMercado);

        List<StockDTO> stocks = new ArrayList<>();

        for (String symbol : symbols) {
            try {
                String response = consultarPrecioDesdeFinnhub(symbol);
                StockDTO dto = parsearRespuesta(response, symbol);
                stocks.add(dto);
            } catch (Exception e) {
                throw new RuntimeException("Error procesando el simbolo: " + symbol, e);
            }
        }

        return stocks;
    }

    @Override
    public MarketStatusDTO getMarketStatus() {
        ZonedDateTime ahora = ZonedDateTime.now(ZoneId.of("America/New_York"));
        boolean abierto = esDiaHabil(ahora) && estaEnHorario(ahora);
        String estado = abierto ? "El mercado esta abierto" : "El mercado esta cerrado";
        return new MarketStatusDTO(abierto, estado);
    }

    @Override
    public StockDTO parseStockResponse(String response, String symbol) {
        return parsearRespuesta(response, symbol);
    }

    private String consultarPrecioDesdeFinnhub(String symbol) {
        try {
            String url = FINNHUB_URL + apiKey;
            String response = restTemplate.getForObject(url, String.class, symbol);
            if (response == null || response.equals("{}")) {
                throw new SymbolNotFoundException("Sin datos para: " + symbol);
            }
            return response;
        } catch (RestClientException e) {
            throw new ExternalServiceException("Error de conexion con Finnhub para: " + symbol, e);
        }
    }

    private StockDTO parsearRespuesta(String response, String symbol) {
        try {
            JSONObject json = new JSONObject(response);
            double currentPrice = json.getDouble("c");
            double highPrice = json.getDouble("h");
            double lowPrice = json.getDouble("l");
            double previousClosePrice = json.getDouble("pc");

            long timestamp = System.currentTimeMillis() / 1000;

            return new StockDTO(
                    symbol,
                    "Descripcion de " + symbol,
                    currentPrice,
                    highPrice,
                    lowPrice,
                    previousClosePrice,
                    timestamp
            );
        } catch (Exception e) {
            throw new MarketDataParseException("No se pudo procesar la respuesta de Finnhub para " + symbol);
        }
    }

    private String[] resolverSimbolos(String mercado) {
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

    private boolean esDiaHabil(ZonedDateTime ahora) {
        DayOfWeek dia = ahora.getDayOfWeek();
        return dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY;
    }

    private boolean estaEnHorario(ZonedDateTime ahora) {
        LocalTime hora = ahora.toLocalTime();
        return hora.isAfter(LocalTime.of(9, 30)) && hora.isBefore(LocalTime.of(16, 0));
    }
}
