package co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions;

public class SaldoException extends RuntimeException {
    public SaldoException(String mensaje) {
        super(mensaje);
    }

    public SaldoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
