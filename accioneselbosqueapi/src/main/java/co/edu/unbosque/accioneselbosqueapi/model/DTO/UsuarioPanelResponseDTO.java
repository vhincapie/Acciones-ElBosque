package co.edu.unbosque.accioneselbosqueapi.model.DTO;

public class UsuarioPanelResponseDTO {

    private String username;
    private String email;
    private String nombre;
    private String rol;
    private String marketInterest;
    private boolean activo;
    private String tipoPlan;
    private String customer;
    private String subscription;

    public UsuarioPanelResponseDTO() {
    }

    public UsuarioPanelResponseDTO(String username, String email, String nombre, String rol, String marketInterest, boolean activo, String tipoPlan, String customer, String subscription) {
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.marketInterest = marketInterest;
        this.activo = activo;
        this.tipoPlan = tipoPlan;
        this.customer = customer;
        this.subscription = subscription;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getMarketInterest() {
        return marketInterest;
    }

    public void setMarketInterest(String marketInterest) {
        this.marketInterest = marketInterest;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getTipoPlan() {
        return tipoPlan;
    }

    public void setTipoPlan(String tipoPlan) {
        this.tipoPlan = tipoPlan;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }
}
