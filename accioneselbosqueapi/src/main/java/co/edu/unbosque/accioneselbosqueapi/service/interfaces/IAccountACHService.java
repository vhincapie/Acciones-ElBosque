package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import java.util.List;

public interface IAccountACHService<T> {

    ResponseAccountACHDTO create(T dto);
    List<AccountACHRelationShipDTO> getAllForAuthenticatedUser();

}
