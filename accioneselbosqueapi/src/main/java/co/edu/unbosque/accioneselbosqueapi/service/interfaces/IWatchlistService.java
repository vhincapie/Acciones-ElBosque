package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;

import java.util.List;
import java.util.Optional;

public interface IWatchlistService {

    WatchlistItemDTO create(WatchlistItemDTO dto);
    void delete(Long id);
    List<WatchlistItemDTO> findAll();
    void updateAlertPrices(Long id, Double minPrice, Double maxPrice);
    List<StockDTO> buscarSimbolos(String query);

}
