package co.edu.unbosque.accioneselbosqueapi.model.DTO;

public class LoginResponseDTO {

    private Long id;
    private String username;
    private String nombre;

    public LoginResponseDTO() {}

    public LoginResponseDTO(Long id, String username, String nombre) {
        this.id = id;
        this.username = username;
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
