package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IOrderAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController implements IOrderAPI {

    private final IOrderService<OrderRequestDTO> orderService;

    public OrderController(IOrderService<OrderRequestDTO> orderService) {
        this.orderService = orderService;
    }

    @Override
    public ResponseEntity<OrderResponseDTO> create(OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO createdOrder = orderService.create(orderRequestDTO);
        return ResponseEntity.ok(createdOrder);
    }
}
