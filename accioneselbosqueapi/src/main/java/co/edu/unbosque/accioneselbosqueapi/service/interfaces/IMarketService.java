package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;

import java.util.List;

public interface IMarketService {

    StockDTO findBySymbol(String symbol);
    List<StockDTO> getRelevantStocks();

    // String findAllSymbols();
}
