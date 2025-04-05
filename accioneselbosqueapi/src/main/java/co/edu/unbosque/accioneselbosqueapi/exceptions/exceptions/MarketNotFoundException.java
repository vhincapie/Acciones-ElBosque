package co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions;

public class MarketNotFoundException extends RuntimeException {
    public MarketNotFoundException(String message) {
        super(message);
    }
}