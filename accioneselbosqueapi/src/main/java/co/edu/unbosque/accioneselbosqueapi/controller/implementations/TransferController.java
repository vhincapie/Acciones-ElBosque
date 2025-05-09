package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.ITransferAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseTransferDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TransferDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ITransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController implements ITransferAPI {

    private final ITransferService<TransferDTO> transferService;

    public TransferController(ITransferService<TransferDTO> transferService) {
        this.transferService = transferService;
    }

    @PreAuthorize("hasRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<ResponseTransferDTO> create(TransferDTO transferDTO) {
        ResponseTransferDTO response = transferService.create(transferDTO);
        return ResponseEntity.ok(response);
    }
}
