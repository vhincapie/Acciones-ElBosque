package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.PortafolioItemDTO;
import java.util.List;
import java.util.Map;

public interface IPortafolioService {

    Map<String, List<PortafolioItemDTO>> getResumenAcciones();
}
