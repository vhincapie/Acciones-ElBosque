package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.PortafolioItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/alpaca/v1")
public interface IPortafolioAPI {

    @GetMapping("/portafolio")
    ResponseEntity<Map<String, List<PortafolioItemDTO>>> getResumenAcciones();

}
