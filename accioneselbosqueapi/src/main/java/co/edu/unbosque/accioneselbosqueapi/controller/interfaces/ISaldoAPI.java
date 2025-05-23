package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.TradingDetailDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/api/saldo/v1")
public interface ISaldoAPI {

    @GetMapping("/obtener")
    ResponseEntity<TradingDetailDTO> obtenerSaldo();
}
