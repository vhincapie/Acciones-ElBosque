package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IUserAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AlpacaAccountUserResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.LoginRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
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
    public ResponseEntity<UserDTO> register(@RequestBody CreateAccountRequestDTO request) {
        UserDTO created = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    public ResponseEntity<String> recoverPassword(String email) {
        userService.sendRecoveryEmail(email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<UserDTO> resetPassword(String token, String newPassword){
    UserDTO updated = userService.resetPassword(token, newPassword);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('INVERSIONISTA', 'COMISIONISTA', 'ADMINISTRADOR')")
    @Override
    public ResponseEntity<UserDTO> getAuthenticatedUserInfo() {
        UserDTO userInfo = userService.getAuthenticatedUserInfo();
        return ResponseEntity.status(HttpStatus.OK).body(userInfo);
    }

    @PreAuthorize("hasAnyRole('INVERSIONISTA', 'COMISIONISTA', 'ADMINISTRADOR')")
    @Override
    public ResponseEntity<AlpacaAccountUserResponseDTO> obtenerCuentaAlpaca() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        AlpacaAccountUserResponseDTO response = alpacaService.obtenerCuentaPorUsuario(username);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    public ResponseEntity<?> login(LoginRequestDTO request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
