package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ExternalServiceException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.MarketDataParseException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SymbolNotFoundException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.MarketStatusDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(MarketService.class);
    private final UserService userService;
    private final RestTemplate restTemplate;

    @Autowired
    public MarketService(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<StockDTO> getRelevantStocks() {
        logger.info("Iniciando obtencion de acciones relevantes para el usuario autenticado");
        User usuario = userService.getAuthenticatedUserEntity();
        String nombreMercado = usuario.getMercadoInteres().getNombre();
        String[] symbols = resolverSimbolos(nombreMercado);
        logger.info("Mercado de interés del usuario: {}", nombreMercado);

        List<StockDTO> stocks = new ArrayList<>();

        for (String symbol : symbols) {
            try {
                logger.info("Consultando precio para el simbolo: {}", symbol);
                String response = consultarPrecioDesdeFinnhub(symbol);
                StockDTO dto = parsearRespuesta(response, symbol);
                stocks.add(dto);
                logger.info("Simbolo procesado exitosamente: {}", symbol);
            } catch (Exception e) {
                logger.error("Error procesando el simbolo: {}", symbol, e);
                throw new RuntimeException("Error procesando el simbolo: " + symbol, e);
            }
        }

        logger.info("Total de acciones obtenidas: {}", stocks.size());
        return stocks;
    }

    @Override
    public MarketStatusDTO getMarketStatus() {
        ZonedDateTime ahora = ZonedDateTime.now(ZoneId.of("America/New_York"));
        boolean abierto = esDiaHabil(ahora) && estaEnHorario(ahora);
        String estado = abierto ? "El mercado esta abierto" : "El mercado esta cerrado";
        logger.info("Consulta de estado de mercado: {}", estado);
        return new MarketStatusDTO(abierto, estado);
    }

    @Override
    public StockDTO parseStockResponse(String response, String symbol) {
        logger.debug("Parseando respuesta para el simbolo: {}", symbol);
        return parsearRespuesta(response, symbol);
    }

    private String consultarPrecioDesdeFinnhub(String symbol) {
        try {
            String url = FINNHUB_URL + apiKey;
            logger.debug("Llamando a la API de Finnhub con la URL: {}", url);
            String response = restTemplate.getForObject(url, String.class, symbol);
            if (response == null || response.equals("{}")) {
                logger.warn("Respuesta vacia o nula para el simbolo: {}", symbol);
                throw new SymbolNotFoundException("Sin datos para: " + symbol);
            }
            return response;
        } catch (RestClientException e) {
            logger.error("Fallo en la conexion con Finnhub para el simbolo: {}", symbol, e);
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

            logger.debug("Datos parseados para {}: precio actual {}, alto {}, bajo {}, cierre anterior {}",
                    symbol, currentPrice, highPrice, lowPrice, previousClosePrice);

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
            logger.error("Error al parsear la respuesta para {}", symbol, e);
            throw new MarketDataParseException("No se pudo procesar la respuesta de Finnhub para " + symbol);
        }
    }

    private String[] resolverSimbolos(String mercado) {
        logger.debug("Resolviendo simbolos para el mercado: {}", mercado);
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
