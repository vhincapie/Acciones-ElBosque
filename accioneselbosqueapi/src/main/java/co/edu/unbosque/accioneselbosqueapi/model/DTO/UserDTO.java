package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import co.edu.unbosque.accioneselbosqueapi.model.Rol;
import co.edu.unbosque.accioneselbosqueapi.model.SubrolAdministrador;

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String nombre;
    private Rol role;
    private SubrolAdministrador subrole;
    private boolean bloqueado;
    private int intentosFallidos;
    private String resetToken;

    public UserDTO() {}

    public UserDTO(Long id, String username, String email, String password, String nombre,
                   Rol role, SubrolAdministrador subrole, boolean bloqueado,
                   int intentosFallidos, String resetToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.role = role;
        this.subrole = subrole;
        this.bloqueado = bloqueado;
        this.intentosFallidos = intentosFallidos;
        this.resetToken = resetToken;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
