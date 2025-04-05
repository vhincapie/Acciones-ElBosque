package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseACHDTO;

public interface IAlpacaACHService<T, K>{

    ResponseACHDTO create(T achRelationShipDTO, String accountId);

}
