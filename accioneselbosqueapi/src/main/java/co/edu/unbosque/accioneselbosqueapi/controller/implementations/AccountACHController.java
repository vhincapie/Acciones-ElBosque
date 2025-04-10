package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IAccountACH;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseACHDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAccountACHService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountACHController implements IAccountACH {

    private final IAccountACHService<AccountACHRelationShipDTO, String> accountACHService;

    public AccountACHController(IAccountACHService<AccountACHRelationShipDTO, String> accountACHService) {
        this.accountACHService = accountACHService;
    }

    @Override
    public ResponseEntity<ResponseAccountACHDTO> create(@RequestBody AccountACHRelationShipDTO accountACHRelationShipDTO,
                                                 @PathVariable String accountId) {
        ResponseAccountACHDTO created = accountACHService.create(accountACHRelationShipDTO, accountId);
        return ResponseEntity.ok(created);
    }
}
