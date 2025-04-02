package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IMarketAPI;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
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
    public ResponseEntity<?> findBySymbol(String symbol) {
        String stockData = marketService.findBySymbol(symbol);
        return ResponseEntity.ok(stockData);
    }

    @Override
    public ResponseEntity<List<?>> getRelevantStocks() {
        List<?> relevantStocks = marketService.getRelevantStocks();
        return ResponseEntity.ok(relevantStocks);
    }

 /*   @Override
    public ResponseEntity<?> findAllSymbols() {
        String allSymbols = marketService.findAllSymbols();
        return ResponseEntity.ok(allSymbols);
    }*/

}
