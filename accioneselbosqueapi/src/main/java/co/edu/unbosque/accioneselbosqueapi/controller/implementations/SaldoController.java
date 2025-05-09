package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.ISaldoAPI;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ISaldoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
public class SaldoController implements ISaldoAPI {

    private final ISaldoService saldoService;

    public SaldoController(ISaldoService saldoService) {
        this.saldoService = saldoService;
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<BigDecimal> obtenerSaldo() {
        BigDecimal saldo = saldoService.obtenerSaldoUsuarioActual();
        return ResponseEntity.ok(saldo);
    }
}
