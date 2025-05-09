package co.edu.unbosque.accioneselbosqueapi.model.DTO;

public class MarketStatusDTO {
    private boolean abierto;
    private String mensaje;

    public MarketStatusDTO() {}

    public MarketStatusDTO(boolean abierto, String mensaje) {
        this.abierto = abierto;
        this.mensaje = mensaje;
    }
    //Getters y Setters
    public boolean isAbierto() {
        return abierto;
    }

    public void setAbierto(boolean abierto) {
        this.abierto = abierto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
