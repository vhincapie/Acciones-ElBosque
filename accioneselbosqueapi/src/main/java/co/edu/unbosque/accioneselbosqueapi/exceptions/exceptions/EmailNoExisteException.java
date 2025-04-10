package co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions;

public class EmailNoExisteException extends RuntimeException {
    public EmailNoExisteException(String message) {
        super(message);
    }
}
