package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface IUserService {

    void register(CreateAccountRequestDTO request);
    void sendRecoveryEmail(String email);
    void resetPassword(String token, String newPassword);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    UserDTO getAuthenticatedUserInfo();
    User getAuthenticatedUserEntity();
}
