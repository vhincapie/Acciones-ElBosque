package co.edu.unbosque.accioneselbosqueapi.exceptions.handler;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.AlpacaAccountCreationException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.AlpacaBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.AlpacaDuplicateAccountException;
import co.edu.unbosque.accioneselbosqueapi.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AlpacaExceptionHandler {

    @ExceptionHandler(AlpacaAccountCreationException.class)
    public ResponseEntity<BaseResponse> handleAccountCreation(AlpacaAccountCreationException ex) {
        return new ResponseEntity<>(new BaseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlpacaBadRequestException.class)
    public ResponseEntity<BaseResponse> handleBadRequest(AlpacaBadRequestException ex) {
        return new ResponseEntity<>(new BaseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlpacaDuplicateAccountException.class)
    public ResponseEntity<BaseResponse> handleAlpacaDuplicateAccountException(AlpacaDuplicateAccountException ex) {
        BaseResponse response = new BaseResponse(ex.getMessage(), 409);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
