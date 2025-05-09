package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.MarketStatusDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import java.util.List;

public interface IMarketService {

    List<StockDTO> getRelevantStocks();
    MarketStatusDTO getMarketStatus();
    StockDTO parseStockResponse(String response, String symbol);
}
