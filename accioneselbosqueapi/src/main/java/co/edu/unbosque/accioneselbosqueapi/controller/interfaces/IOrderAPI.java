package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/alpaca/v1")
public interface IOrderAPI {

    @PostMapping("/orders")
    ResponseEntity<OrderResponseDTO> create(@RequestBody OrderRequestDTO orderRequestDTO);
}
