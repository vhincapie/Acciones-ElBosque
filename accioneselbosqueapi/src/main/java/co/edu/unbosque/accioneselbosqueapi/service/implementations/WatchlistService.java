package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.*;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.WatchlistItem;
import co.edu.unbosque.accioneselbosqueapi.repository.IWatchlistRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IWatchlistService;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WatchlistService implements IWatchlistService {

    private final IWatchlistRepository repository;
    private final ModelMapper modelMapper;
    private final MarketService marketService;

    public WatchlistService(IWatchlistRepository repository, ModelMapper modelMapper, MarketService marketService) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.marketService = marketService;
    }

    @Override
    public WatchlistItemDTO create(WatchlistItemDTO dto) {
        try {
            WatchlistItem entity = modelMapper.map(dto, WatchlistItem.class);
            entity.setCompanyName("Descripcion de " + dto.getSymbol());
            entity.setCreatedAt(LocalDateTime.now());

            WatchlistItem saved = repository.save(entity);
            return modelMapper.map(saved, WatchlistItemDTO.class);
        } catch (Exception e) {
            throw new WatchlistBadRequestException("Error al crear el ítem en la watchlist: " + e.getMessage());
        }
    }


    @Override
    public Optional<WatchlistItemDTO> find(Long id) {
        return Optional.of(repository.findById(id)
                .map(item -> modelMapper.map(item, WatchlistItemDTO.class))
                .orElseThrow(() -> new WatchlistNotFoundException("Item no encontrado con ID: " + id)));
    }

    @Override
    public WatchlistItemDTO update(Long id, WatchlistItemDTO dto) {
        if (!repository.existsById(id)) {
            throw new WatchlistNotFoundException("No se encontro el ítem de la watchlist con ID: " + id);
        }

        validarCampos(dto);
        dto.setId(id);

        try {
            WatchlistItem entity = modelMapper.map(dto, WatchlistItem.class);
            WatchlistItem updated = repository.save(entity);
            return modelMapper.map(updated, WatchlistItemDTO.class);
        } catch (Exception e) {
            throw new WatchlistBadRequestException("Error al actualizar el item: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new WatchlistNotFoundException("No se encontro el item de la watchlist con ID: " + id);
        }

        try {
            repository.deleteById(id);
        } catch (Exception e) {
            throw new WatchlistBadRequestException("No se pudo eliminar el item de la watchlist.");
        }
    }

    @Override
    public List<WatchlistItemDTO> findAll() {
        List<WatchlistItem> items = repository.findAll();

        if (items.isEmpty()) {
            throw new WatchlistNotFoundException("No hay elementos en la watchlist.");
        }

        return items.stream()
                .map(item -> modelMapper.map(item, WatchlistItemDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updateAlertPrices(Long id, Double minPrice, Double maxPrice) {
        WatchlistItem item = repository.findById(id)
                .orElseThrow(() -> new WatchlistNotFoundException("No se encontro el item de la watchlist con ID: " + id));

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new WatchlistBadRequestException("El precio minimo no puede ser mayor que el maximo.");
        }

        item.setAlertMinPrice(minPrice);
        item.setAlertMaxPrice(maxPrice);

        try {
            repository.save(item);
        } catch (Exception e) {
            throw new WatchlistBadRequestException("Error al actualizar precios de alerta: " + e.getMessage());
        }
    }

    private void validarCampos(WatchlistItemDTO dto) {
        if (dto.getSymbol() == null || dto.getSymbol().isBlank()) {
            throw new WatchlistBadRequestException("El simbolo (symbol) es obligatorio.");
        }
        if (dto.getCompanyName() == null || dto.getCompanyName().isBlank()) {
            throw new WatchlistBadRequestException("El nombre de la empresa es obligatorio.");
        }
        if (dto.getAlertMinPrice() != null && dto.getAlertMaxPrice() != null &&
                dto.getAlertMinPrice() > dto.getAlertMaxPrice()) {
            throw new WatchlistBadRequestException("El precio minimo no puede ser mayor al precio maximo.");
        }
    }

    @Scheduled(fixedRate = 60000)
    public void verificarAlertas() {
        List<WatchlistItem> items = repository.findAll();

        for (WatchlistItem item : items) {
            try {
                StockDTO stock = marketService.findBySymbol(item.getSymbol());
                double current = stock.getCurrentPrice();

                if (item.getAlertMinPrice() != null && current < item.getAlertMinPrice()) {
                    System.out.println("ALERTA: " + item.getSymbol() + " bajo de " + item.getAlertMinPrice() + " (actual: " + current + ")");
                }

                if (item.getAlertMaxPrice() != null && current > item.getAlertMaxPrice()) {
                    System.out.println("ALERTA: " + item.getSymbol() + " supero " + item.getAlertMaxPrice() + " (actual: " + current + ")");
                }

            } catch (MarketConnectionException e) {
                System.out.println("Conexion fallida con Finnhub para " + item.getSymbol() + ": " + e.getMessage());
            } catch (MarketDataParseException e) {
                System.out.println("Error al interpretar los datos para " + item.getSymbol() + ": " + e.getMessage());
            } catch (SymbolNotFoundException e) {
                System.out.println("Simbolo no encontrado en Finnhub: " + item.getSymbol());
            } catch (Exception e) {
                System.out.println("Error inesperado al verificar alerta para " + item.getSymbol() + ": " + e.getMessage());
            }

        }
    }

}
