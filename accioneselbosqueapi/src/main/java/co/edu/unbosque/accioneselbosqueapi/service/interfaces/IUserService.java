package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface IUserService extends UserDetailsService {

    UserDTO register(CreateAccountRequestDTO request);
    LoginResponseDTO login(LoginRequestDTO request);
    Authentication autenticar(String username, String password);
    void sendRecoveryEmail(String email);
    UserDTO resetPassword(String token, String newPassword);
    User getAuthenticatedUserEntity();
    UsuarioPanelResponseDTO getPanelInfo();

}
