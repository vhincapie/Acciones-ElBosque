package co.edu.unbosque.accioneselbosqueapi.model.DTO;

public class CheckoutResponseDTO {

    private String url;
    private String mensaje;

    public CheckoutResponseDTO() {
    }

    public CheckoutResponseDTO(String url) {
        this.url = url;
    }

    public CheckoutResponseDTO(String url, String mensaje) {
        this.url = url;
        this.mensaje = mensaje;
    }

    //Getters y Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
