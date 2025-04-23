package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {

    UserDTO register(CreateAccountRequestDTO request);
    void sendRecoveryEmail(String email);
    UserDTO resetPassword(String token, String newPassword);
    UserDTO getAuthenticatedUserInfo();
    User getAuthenticatedUserEntity();
}
