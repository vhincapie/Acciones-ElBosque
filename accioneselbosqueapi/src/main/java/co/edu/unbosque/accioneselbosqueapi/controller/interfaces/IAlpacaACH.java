package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseACHDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/ach/v1")
public interface IAlpacaACH {

    @PostMapping("/{accountId}/recipient_banks")
    ResponseEntity<ResponseACHDTO> create(@RequestBody ACHRelationShipDTO achRelationShipDTO, @PathVariable String accountId);

}
