package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@RequestMapping("/api/saldo")
public interface ISaldoAPI {

    @GetMapping("/obtener")
    ResponseEntity<BigDecimal> obtenerSaldo();
}
