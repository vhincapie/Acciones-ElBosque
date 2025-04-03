package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;

public interface IWatchlistService extends IService<WatchlistItemDTO, Long> {

    void updateAlertPrices(Long id, Double minPrice, Double maxPrice);
}
