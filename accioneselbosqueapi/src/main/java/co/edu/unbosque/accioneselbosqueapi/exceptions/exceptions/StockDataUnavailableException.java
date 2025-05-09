package co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions;

public class StockDataUnavailableException extends RuntimeException {
    public StockDataUnavailableException(String message) {
        super(message);
    }

    public StockDataUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
