package co.edu.unbosque.accioneselbosqueapi.security;

public class LoginSuccessResponse {

    private String message;
    private String username;
    private String role;

    public LoginSuccessResponse(String message, String username, String role) {
        this.message = message;
        this.username = username;
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}