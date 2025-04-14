package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;

public interface IAccountACHService<T> {

    ResponseAccountACHDTO create(T dto);
}
