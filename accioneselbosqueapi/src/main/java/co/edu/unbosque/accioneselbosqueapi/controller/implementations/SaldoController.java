package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.ISaldoAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TradingDetailDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ISaldoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaldoController implements ISaldoAPI {

    private final ISaldoService saldoService;

    public SaldoController(ISaldoService saldoService) {
        this.saldoService = saldoService;
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<TradingDetailDTO> obtenerSaldo() {
        TradingDetailDTO detalle = saldoService.obtenerSaldoUsuarioActual();
        return ResponseEntity.ok(detalle);
    }


}
