package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/alpaca/v1")
public interface IAccountACH {

    @PostMapping("/ach_relationships")
    ResponseEntity<ResponseAccountACHDTO> create(@RequestBody AccountACHRelationShipDTO dto);

    @GetMapping("/ach_relationships")
    ResponseEntity<List<AccountACHRelationShipDTO>> getAllForAuthenticatedUser();


}
