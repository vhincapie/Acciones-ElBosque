package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth/v1")
public interface IUserAPI {

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody CreateAccountRequestDTO request);

    @PostMapping("/recover")
    ResponseEntity<String> recoverPassword(@RequestParam String email);

    @PostMapping("/reset-password")
    ResponseEntity<UserDTO> resetPassword(@RequestParam String token, @RequestParam String newPassword);

    @GetMapping("/panel")
    ResponseEntity<UsuarioPanelResponseDTO> getAuthenticatedUserInfo();

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequestDTO request);

}
