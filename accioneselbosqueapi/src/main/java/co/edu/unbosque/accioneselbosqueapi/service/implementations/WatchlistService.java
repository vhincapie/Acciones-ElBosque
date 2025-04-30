package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.*;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.model.entity.UserSubscription;
import co.edu.unbosque.accioneselbosqueapi.model.entity.WatchlistItem;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserSubscriptionRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IWatchlistRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IWatchlistService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WatchlistService implements IWatchlistService {

    @Value("${finnhub.api.key}")
    private String apiKey;

    private static final String SYMBOL_LOOKUP_URL = "https://finnhub.io/api/v1/stock/symbol?exchange=US&token=";
    private static final String SYMBOL_SEARCH_URL = "https://finnhub.io/api/v1/search?q=";
    private static final String SYMBOL_SYMBOL_URL = "https://finnhub.io/api/v1/quote?symbol=";
    private static final String FINNHUB_URL = "https://finnhub.io/api/v1/quote?symbol={symbol}&token=";

    private final IWatchlistRepository repository;
    private final ModelMapper modelMapper;
    private final MarketService marketService;
    private final UserService userService;
    private final IUserSubscriptionRepository subscriptionRepository;

    public WatchlistService(
            IWatchlistRepository repository,
            ModelMapper modelMapper,
            MarketService marketService,
            UserService userService,
            IUserSubscriptionRepository subscriptionRepository) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.marketService = marketService;
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public WatchlistItemDTO create(WatchlistItemDTO dto) {
        User user = validarUsuarioPremium();
        validarCampos(dto);
        validarSimbolo(dto.getSymbol());

        if (repository.existsByUserAndSymbol(user, dto.getSymbol())) {
            throw new WatchlistBadRequestException("Este simbolo ya esta registrado en tu watchlist.");
        }

        dto.setUser(user.getId());
        dto.setCompanyName("Descripcion de " + dto.getSymbol());

        WatchlistItem entity = modelMapper.map(dto, WatchlistItem.class);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUser(user);

        WatchlistItem saved = repository.save(entity);
        return modelMapper.map(saved, WatchlistItemDTO.class);
    }

    @Override
    public void delete(Long id) {
        User user = validarUsuarioPremium();
        WatchlistItem item = repository.findById(id)
                .orElseThrow(() -> new WatchlistNotFoundException("No se encontro el item con ID: " + id));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("No tienes permisos para eliminar este item.");
        }

        repository.deleteById(id);
    }

    @Override
    public List<WatchlistItemDTO> findAll() {
        User user = validarUsuarioPremium();

        return repository.findAll().stream()
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .map(item -> {
                    WatchlistItemDTO dto = modelMapper.map(item, WatchlistItemDTO.class);

                    try {
                        StockDTO precios = findBySymbol(item.getSymbol());
                        dto.setCurrentPrice(precios.getCurrentPrice());
                        dto.setHighPrice(precios.getHighPrice());
                        dto.setLowPrice(precios.getLowPrice());
                        dto.setPreviousClosePrice(precios.getPreviousClosePrice());
                    } catch (Exception e) {
                        System.err.println("No se pudieron cargar precios para " + item.getSymbol() + ": " + e.getMessage());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void updateAlertPrices(Long id, Double minPrice, Double maxPrice) {
        User user = validarUsuarioPremium();
        WatchlistItem item = repository.findById(id)
                .orElseThrow(() -> new WatchlistNotFoundException("Item no encontrado con ID: " + id));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException("No tienes permisos para actualizar este item.");
        }

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new WatchlistBadRequestException("El precio minimo no puede ser mayor al maximo.");
        }

        item.setAlertMinPrice(minPrice);
        item.setAlertMaxPrice(maxPrice);
        repository.save(item);
    }

    @Override
    public List<StockDTO> buscarSimbolos(String query) {
        RestTemplate restTemplate = new RestTemplate();
        List<StockDTO> suggestions = new ArrayList<>();
        String lowerQuery = query.toLowerCase();


        try {
            String searchUrl = SYMBOL_SEARCH_URL + query + "&token=" + apiKey;
            String response = restTemplate.getForObject(searchUrl, String.class);
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("result");

            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                String symbol = item.optString("symbol", "");
                String description = item.optString("description", "");

                if (!symbol.contains(".")) {
                    StockDTO dto = buildStockDTO(restTemplate, symbol, description);
                    if (dto != null) suggestions.add(dto);
                }
            }


            if (!suggestions.isEmpty()) return suggestions;

        } catch (Exception e) {
            throw new MarketServiceUnavailableException("Error al buscar por simbolo en Finnhub: " + e.getMessage());
        }

        try {
            String url = SYMBOL_LOOKUP_URL + apiKey;
            String response = restTemplate.getForObject(url, String.class);
            JSONArray array = new JSONArray(response);

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String symbol = item.optString("symbol", "");
                String description = item.optString("description", "");
                String mic = item.optString("mic", "");

                if (!symbol.contains(".") &&
                        (description.toLowerCase().contains(lowerQuery) ||
                                symbol.toLowerCase().contains(lowerQuery)) &&
                        (mic.equalsIgnoreCase("XNYS") || mic.equalsIgnoreCase("XNAS"))) {

                    StockDTO dto = buildStockDTO(restTemplate, symbol, description);
                    if (dto != null) suggestions.add(dto);
                }
            }

        } catch (Exception e) {
            throw new MarketServiceUnavailableException("No se pudieron buscar simbolos: " + e.getMessage());
        }

        return suggestions;
    }

    public StockDTO buildStockDTO(RestTemplate restTemplate, String symbol, String description) {
        try {
            String quoteUrl = SYMBOL_SYMBOL_URL + symbol + "&token=" + apiKey;
            String quoteResponse = restTemplate.getForObject(quoteUrl, String.class);
            JSONObject quoteJson = new JSONObject(quoteResponse);

            StockDTO dto = new StockDTO();
            dto.setSymbol(symbol);
            dto.setDescription(description);
            dto.setCurrentPrice(quoteJson.optDouble("c", 0.0));
            dto.setHighPrice(quoteJson.optDouble("h", 0.0));
            dto.setLowPrice(quoteJson.optDouble("l", 0.0));
            dto.setPreviousClosePrice(quoteJson.optDouble("pc", 0.0));
            dto.setTimestamp(quoteJson.optLong("t", 0));
            return dto;

        } catch (Exception e) {
            return null;
        }
    }

    public void validarCampos(WatchlistItemDTO dto) {
        if (dto.getSymbol() == null || dto.getSymbol().isBlank()) {
            throw new WatchlistBadRequestException("El simbolo es obligatorio.");
        }
        if (dto.getCompanyName() == null || dto.getCompanyName().isBlank()) {
            throw new WatchlistBadRequestException("El nombre de la empresa es obligatorio.");
        }
        if (dto.getAlertMinPrice() != null && dto.getAlertMaxPrice() != null &&
                dto.getAlertMinPrice() > dto.getAlertMaxPrice()) {
            throw new WatchlistBadRequestException("El precio minimo no puede ser mayor al maximo.");
        }
    }

    public User validarUsuarioPremium() {
        User user = userService.getAuthenticatedUserEntity();
        return subscriptionRepository.findByUser(user)
                .filter(UserSubscription::isActivo)
                .map(sub -> user)
                .orElseThrow(() -> new UnauthorizedAccessException("Solo los usuarios premium pueden acceder a esta funcionalidad."));
    }

    public void validarSimbolo(String symbol) {
        String normalizedSymbol = symbol.trim().toUpperCase();
        String url = SYMBOL_LOOKUP_URL + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(url, String.class);
            JSONArray array = new JSONArray(response);

            boolean encontrado = false;

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String mic = item.optString("mic", "");
                String symbolFromAPI = item.getString("symbol").trim().toUpperCase();

                if (symbolFromAPI.equals(normalizedSymbol)
                        && (mic.equalsIgnoreCase("XNYS") || mic.equalsIgnoreCase("XNAS"))) {
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                throw new SymbolNotFoundException("El simbolo '" + symbol + "' no es valido o no pertenece a NASDAQ o NYSE.");
            }

        } catch (Exception e) {
            throw new MarketServiceUnavailableException("Error al validar simbolo en Finnhub: " + e.getMessage());
        }
    }

    public StockDTO findBySymbol(String symbol) {
        String url = FINNHUB_URL + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(url, String.class, symbol);

            if (response == null || response.equals("{}")) {
                throw new MarketNotFoundException("No se encontraron datos de mercado para el simbolo: " + symbol);
            }

            return marketService.parseStockResponse(response, symbol);

        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new MarketServiceUnavailableException("Limite de uso de API alcanzado. Intenta nuevamente mas tarde.");
        } catch (RestClientException e) {
            throw new MarketConnectionException("Error de conexion al consultar el simbolo: " + symbol);
        } catch (Exception e) {
            throw new MarketServiceUnavailableException("Error inesperado al consultar el simbolo " + symbol + ": " + e.getMessage());
        }
    }



}
