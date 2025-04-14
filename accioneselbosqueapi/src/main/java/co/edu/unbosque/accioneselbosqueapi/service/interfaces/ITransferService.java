package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseTransferDTO;

public interface ITransferService<T>{

    ResponseTransferDTO create(T transferDTO);
}
