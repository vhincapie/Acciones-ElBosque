package co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions;

public class OrderSyncException extends RuntimeException {

    public OrderSyncException(String message) {
        super(message);
    }

    public OrderSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
