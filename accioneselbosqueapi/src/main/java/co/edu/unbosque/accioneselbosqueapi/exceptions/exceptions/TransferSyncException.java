package co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions;

public class TransferSyncException extends RuntimeException {
    public TransferSyncException(String message) {
        super(message);
    }

    public TransferSyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
