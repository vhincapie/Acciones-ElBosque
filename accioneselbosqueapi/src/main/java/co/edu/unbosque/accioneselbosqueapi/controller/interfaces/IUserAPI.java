package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.AlpacaAccountUserResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.LoginRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
public interface IUserAPI {


    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody CreateAccountRequestDTO request);

    @PostMapping("/recover")
    ResponseEntity<String> recoverPassword(@RequestParam String email);

    @PostMapping("/reset-password")
    ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword);

    @GetMapping("/panel")
    ResponseEntity<UserDTO> getAuthenticatedUserInfo();

    @GetMapping("/cuenta-alpaca")
    ResponseEntity<AlpacaAccountUserResponseDTO> obtenerCuentaAlpaca();

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequestDTO request);
}
