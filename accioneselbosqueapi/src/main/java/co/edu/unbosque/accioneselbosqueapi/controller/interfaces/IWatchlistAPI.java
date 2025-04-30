package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/watchlist/v1")
public interface IWatchlistAPI {

    @PostMapping("/item")
    ResponseEntity<WatchlistItemDTO> create(@RequestBody WatchlistItemDTO dto);

    @DeleteMapping("/item/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @GetMapping("/items")
    ResponseEntity<List<WatchlistItemDTO>> findAll();

    @PutMapping("/item/{id}/alerts")
    ResponseEntity<String> updateAlertPrices(@PathVariable Long id,
                                             @RequestParam Double min,
                                             @RequestParam Double max);

    @GetMapping("/search/{query}")
    ResponseEntity<List<StockDTO>> buscarSimbolos(@PathVariable String query);

}
