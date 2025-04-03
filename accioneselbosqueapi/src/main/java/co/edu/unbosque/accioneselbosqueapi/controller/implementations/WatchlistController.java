package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IWatchlistAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IWatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WatchlistController implements IWatchlistAPI {

    @Autowired
    private IWatchlistService watchlistService;

    @Override
    public ResponseEntity<WatchlistItemDTO> find(Long id) {
        return watchlistService.find(id)
                .map(dto -> ResponseEntity.status(HttpStatus.OK).body(dto))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Override
    public ResponseEntity<List<WatchlistItemDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(watchlistService.findAll());
    }

    @Override
    public ResponseEntity<WatchlistItemDTO> create(WatchlistItemDTO dto) {
        WatchlistItemDTO created = watchlistService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<WatchlistItemDTO> update(Long id, WatchlistItemDTO dto) {
        WatchlistItemDTO updated = watchlistService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        watchlistService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<String> updateAlertPrices(Long id, Double min, Double max) {
        watchlistService.updateAlertPrices(id, min, max);
        return ResponseEntity.status(HttpStatus.OK).body("Precios de alerta actualizados.");
    }
}
