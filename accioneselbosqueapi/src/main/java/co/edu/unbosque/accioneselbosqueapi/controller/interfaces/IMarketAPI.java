package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

@RequestMapping("/api/mercado/v1")
public interface IMarketAPI {

    @GetMapping ("/stock-data/{symbol}")
    ResponseEntity<?> findBySymbol(@PathVariable String symbol);

    @GetMapping("/stock-relevant")
    ResponseEntity<List<?>> getRelevantStocks();

  /*  @GetMapping("/stock-data/all")
    ResponseEntity<?> findAllSymbols();*/

}
