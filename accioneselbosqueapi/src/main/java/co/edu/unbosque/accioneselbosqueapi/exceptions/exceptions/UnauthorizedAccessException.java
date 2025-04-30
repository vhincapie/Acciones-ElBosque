package co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions;

public class UnauthorizedAccessException extends RuntimeException{

    public UnauthorizedAccessException(String message){
        super(message);
    }
}
