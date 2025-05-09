package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IPortafolioAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.PortafolioItemDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IPortafolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class PortafolioController implements IPortafolioAPI {

    private final IPortafolioService portfolioService;

    public PortafolioController(IPortafolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<Map<String, List<PortafolioItemDTO>>> getResumenAcciones() {
        return ResponseEntity.ok(portfolioService.getResumenAcciones());
    }
}
