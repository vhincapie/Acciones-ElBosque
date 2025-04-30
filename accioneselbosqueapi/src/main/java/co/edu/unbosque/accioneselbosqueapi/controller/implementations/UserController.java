package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IUserAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController implements IUserAPI {

    private final IUserService userService;

    public UserController(IUserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
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
    public ResponseEntity<UsuarioPanelResponseDTO> getAuthenticatedUserInfo() {
        UsuarioPanelResponseDTO userInfo = userService.getPanelInfo();
        return ResponseEntity.ok(userInfo);
    }

    @Override
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = userService.login(request);
        return ResponseEntity.ok(response);
    }


}
