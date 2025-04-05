package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IAlpacaACH;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseACHDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAlpacaACHService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AlpacaACHController implements IAlpacaACH {

    private final IAlpacaACHService<ACHRelationShipDTO, Integer> alpacaACHService;

    public AlpacaACHController(IAlpacaACHService<ACHRelationShipDTO, Integer> alpacaACHService) {
        this.alpacaACHService = alpacaACHService;
    }

    @Override
    public ResponseEntity<ResponseACHDTO> create(@RequestBody ACHRelationShipDTO achRelationShipDTO,
                                                 @PathVariable String accountId) {
        ResponseACHDTO created = alpacaACHService.create(achRelationShipDTO, accountId);
        return ResponseEntity.ok(created);
    }

}
