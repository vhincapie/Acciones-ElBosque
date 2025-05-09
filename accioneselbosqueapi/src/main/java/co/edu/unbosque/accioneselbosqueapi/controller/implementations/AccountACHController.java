package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IAccountACH;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAccountACHService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountACHController implements IAccountACH {

    private final IAccountACHService<AccountACHRelationShipDTO> accountACHService;

    public AccountACHController(IAccountACHService<AccountACHRelationShipDTO> accountACHService) {
        this.accountACHService = accountACHService;
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<ResponseAccountACHDTO> create(@RequestBody AccountACHRelationShipDTO dto) {
        ResponseAccountACHDTO created = accountACHService.create(dto);
        return ResponseEntity.ok(created);
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<List<AccountACHRelationShipDTO>> getAllForAuthenticatedUser() {
        List<AccountACHRelationShipDTO> list = accountACHService.getAllForAuthenticatedUser();
        return ResponseEntity.ok(list);
    }

}
