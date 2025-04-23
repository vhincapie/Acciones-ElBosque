package co.edu.unbosque.accioneselbosqueapi.exceptions.handler;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SuscripcionException;
import co.edu.unbosque.accioneselbosqueapi.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SuscripcionExceptionHandler {

    @ExceptionHandler(SuscripcionException.class)
    public ResponseEntity<BaseResponse> handleSuscripcionException(SuscripcionException ex) {
        BaseResponse response = new BaseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
