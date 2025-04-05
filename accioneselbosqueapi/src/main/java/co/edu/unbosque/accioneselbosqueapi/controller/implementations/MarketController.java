package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IMarketAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MarketController implements IMarketAPI {

    private final IMarketService marketService;

    public MarketController(IMarketService marketService) {
        this.marketService = marketService;
    }

    @Override
    public ResponseEntity<StockDTO> findBySymbol(String symbol) {
        StockDTO dto = marketService.findBySymbol(symbol);
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(dto);
    }

    @Override
    public ResponseEntity<List<StockDTO>> getRelevantStocks() {
        List<StockDTO> stockList = marketService.getRelevantStocks();
        return ResponseEntity
                .status(HttpStatus.OK.value())
                .body(stockList);
    }

 /*   @Override
    public ResponseEntity<?> findAllSymbols() {
        String allSymbols = marketService.findAllSymbols();
        return ResponseEntity.ok(allSymbols);
    }*/

}
