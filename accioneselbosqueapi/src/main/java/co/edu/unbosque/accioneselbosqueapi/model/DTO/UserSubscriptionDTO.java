package co.edu.unbosque.accioneselbosqueapi.model.DTO;


import java.time.LocalDate;

public class UserSubscriptionDTO {

    private Long id;
    private Long user;
    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private boolean activo;
    private LocalDate fechaInicio;
    private LocalDate fechaCancelacion;
    private String tipoPlan;

    public UserSubscriptionDTO() {
    }

    public UserSubscriptionDTO(Long id, Long user, String stripeCustomerId, String stripeSubscriptionId, boolean activo, LocalDate fechaInicio, LocalDate fechaCancelacion, String tipoPlan) {
        this.id = id;
        this.user = user;
        this.stripeCustomerId = stripeCustomerId;
        this.stripeSubscriptionId = stripeSubscriptionId;
        this.activo = activo;
        this.fechaInicio = fechaInicio;
        this.fechaCancelacion = fechaCancelacion;
        this.tipoPlan = tipoPlan;
    }

    //Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public String getStripeSubscriptionId() {
        return stripeSubscriptionId;
    }

    public void setStripeSubscriptionId(String stripeSubscriptionId) {
        this.stripeSubscriptionId = stripeSubscriptionId;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaCancelacion() {
        return fechaCancelacion;
    }

    public void setFechaCancelacion(LocalDate fechaCancelacion) {
        this.fechaCancelacion = fechaCancelacion;
    }

    public String getTipoPlan() {
        return tipoPlan;
    }

    public void setTipoPlan(String tipoPlan) {
        this.tipoPlan = tipoPlan;
    }
}