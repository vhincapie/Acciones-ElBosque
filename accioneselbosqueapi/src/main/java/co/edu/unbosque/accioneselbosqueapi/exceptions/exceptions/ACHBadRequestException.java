package co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions;

import org.springframework.http.HttpStatus;

public class ACHBadRequestException extends RuntimeException {

    public ACHBadRequestException(String message) {
        super(message);
    }
}
