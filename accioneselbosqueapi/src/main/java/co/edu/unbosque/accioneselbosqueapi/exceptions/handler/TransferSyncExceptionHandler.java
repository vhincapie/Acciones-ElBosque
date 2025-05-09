package co.edu.unbosque.accioneselbosqueapi.exceptions.handler;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.TransferSyncException;
import co.edu.unbosque.accioneselbosqueapi.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TransferSyncExceptionHandler {

    @ExceptionHandler(TransferSyncException.class)
    public ResponseEntity<BaseResponse> handleTransferSync(TransferSyncException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
