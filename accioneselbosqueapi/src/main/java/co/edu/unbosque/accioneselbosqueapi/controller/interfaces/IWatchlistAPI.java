package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/watchlist/v1")
public interface IWatchlistAPI {

    @GetMapping("/item/{id}")
    ResponseEntity<WatchlistItemDTO> find(@PathVariable Long id);

    @GetMapping("/items")
    ResponseEntity<List<WatchlistItemDTO>> findAll();

    @PostMapping("/item")
    ResponseEntity<WatchlistItemDTO> create(@RequestBody WatchlistItemDTO dto);

    @PutMapping("/item/{id}")
    ResponseEntity<WatchlistItemDTO> update(@PathVariable Long id, @RequestBody WatchlistItemDTO dto);

    @DeleteMapping("/item/{id}")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @PutMapping("/item/{id}/alerts")
    ResponseEntity<String> updateAlertPrices(@PathVariable Long id,
                                             @RequestParam Double min,
                                             @RequestParam Double max);
}