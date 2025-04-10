package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IUserAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AlpacaAccountUserResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.LoginRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAlpacaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements IUserAPI {

    private final IUserService userService;
    private final IAlpacaService alpacaService;
    @Autowired
    private AuthenticationManager authenticationManager;

    public UserController(IUserService userService, IAlpacaService alpacaService) {
        this.userService = userService;
        this.alpacaService = alpacaService;
    }

    @Override
    public ResponseEntity<?> register(@RequestBody CreateAccountRequestDTO request) {
        userService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Usuario registrado exitosamente y cuenta Alpaca creada");
    }

    @Override
    public ResponseEntity<String> recoverPassword(String email) {
        userService.sendRecoveryEmail(email);
        return ResponseEntity.ok("Se ha enviado un correo con instrucciones para recuperar la contraseña");
    }

    @Override
    public ResponseEntity<String> resetPassword(String token, String newPassword) {
        userService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    @PreAuthorize("hasAnyRole('INVERSIONISTA', 'COMISIONISTA', 'ADMINISTRADOR')")
    @Override
    public ResponseEntity<UserDTO> getAuthenticatedUserInfo() {
        UserDTO userInfo = userService.getAuthenticatedUserInfo();
        return ResponseEntity.ok(userInfo);
    }

    @PreAuthorize("hasAnyRole('INVERSIONISTA', 'COMISIONISTA', 'ADMINISTRADOR')")
    @Override
    public ResponseEntity<AlpacaAccountUserResponseDTO> obtenerCuentaAlpaca() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        AlpacaAccountUserResponseDTO response = alpacaService.obtenerCuentaPorUsuario(username);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok("Inicio de sesión exitoso");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }
}
