package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseTransferDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TransferDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/alpaca/v1")
public interface ITransferAPI {

    @PostMapping("/transfers")
    ResponseEntity<ResponseTransferDTO> create(@RequestBody TransferDTO transferDTO);


}
