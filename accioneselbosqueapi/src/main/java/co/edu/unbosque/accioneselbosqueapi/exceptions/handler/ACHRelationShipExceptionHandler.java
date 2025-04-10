package co.edu.unbosque.accioneselbosqueapi.exceptions.handler;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHConflictException;
import co.edu.unbosque.accioneselbosqueapi.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ACHRelationShipExceptionHandler {

    @ExceptionHandler(ACHBadRequestException.class)
    public ResponseEntity<BaseResponse> handleACHBadRequest(ACHBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(ACHConflictException.class)
    public ResponseEntity<BaseResponse> handleACHConflict(ACHConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.CONFLICT.value()));
    }
}
