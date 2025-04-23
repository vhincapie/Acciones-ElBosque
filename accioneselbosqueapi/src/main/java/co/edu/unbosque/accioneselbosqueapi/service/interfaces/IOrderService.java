package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.OrderResponseDTO;

public interface IOrderService<T> {

    OrderResponseDTO create(T orderRequestDTO);
}
