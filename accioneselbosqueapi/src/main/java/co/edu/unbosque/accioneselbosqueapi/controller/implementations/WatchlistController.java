package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IWatchlistAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IWatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WatchlistController implements IWatchlistAPI {

    private final IWatchlistService service;

    public WatchlistController(IWatchlistService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<WatchlistItemDTO> create(WatchlistItemDTO dto) {
        WatchlistItemDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<Void> delete(Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<List<WatchlistItemDTO>> findAll() {
        List<WatchlistItemDTO> result = service.findAll();
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<String> updateAlertPrices(Long id, Double min, Double max) {
        service.updateAlertPrices(id, min, max);
        return ResponseEntity.ok("Precios de alerta actualizados.");
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<List<StockDTO>> buscarSimbolos(String query) {
        List<StockDTO> results = service.buscarSimbolos(query);
        return ResponseEntity.ok(results);
    }


}
