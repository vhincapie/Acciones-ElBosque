package co.edu.unbosque.accioneselbosqueapi.model.entity;

import co.edu.unbosque.accioneselbosqueapi.model.Rol;
import co.edu.unbosque.accioneselbosqueapi.model.SubrolAdministrador;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "nombre")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private Rol role;

    @Enumerated(EnumType.STRING)
    @Column(name = "subrol")
    private SubrolAdministrador subrole;

    @Column(name = "bloqueado")
    private boolean bloqueado = false;

    @Column(name = "intentos_fallidos")
    private int intentosFallidos = 0;

    @Column(name = "reset_token")
    private String resetToken;

    @ManyToOne
    @JoinColumn(name = "id_mercado")
    private MarketInterest mercadoInteres;


    public User() {}

    public User(Long id, String username, String password, String email, String nombre, Rol role, SubrolAdministrador subrole, boolean bloqueado, int intentosFallidos, String resetToken, MarketInterest mercadoInteres) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.nombre = nombre;
        this.role = role;
        this.subrole = subrole;
        this.bloqueado = bloqueado;
        this.intentosFallidos = intentosFallidos;
        this.resetToken = resetToken;
        this.mercadoInteres = mercadoInteres;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Rol getRole() {
        return role;
    }

    public void setRole(Rol role) {
        this.role = role;
    }

    public SubrolAdministrador getSubrole() {
        return subrole;
    }

    public void setSubrole(SubrolAdministrador subrole) {
        this.subrole = subrole;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public MarketInterest getMercadoInteres() {
        return mercadoInteres;
    }

    public void setMercadoInteres(MarketInterest mercadoInteres) {
        this.mercadoInteres = mercadoInteres;
    }
}
