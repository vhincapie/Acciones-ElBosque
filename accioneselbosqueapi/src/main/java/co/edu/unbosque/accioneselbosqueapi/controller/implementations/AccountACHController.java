package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IAccountACH;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAccountACHService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountACHController implements IAccountACH {

    private final IAccountACHService<AccountACHRelationShipDTO> accountACHService;

    public AccountACHController(IAccountACHService<AccountACHRelationShipDTO> accountACHService) {
        this.accountACHService = accountACHService;
    }

    @Override
    public ResponseEntity<ResponseAccountACHDTO> create(@RequestBody AccountACHRelationShipDTO dto) {
        ResponseAccountACHDTO created = accountACHService.create(dto);
        return ResponseEntity.ok(created);
    }
}
