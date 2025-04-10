package co.edu.unbosque.accioneselbosqueapi.security;

import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final IUserRepository userRepository;

    public LoginFailureHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {

        String username = request.getParameter("username");

        String mensaje;

        if (exception instanceof BadCredentialsException && username != null) {
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                int intentos = user.getIntentosFallidos() + 1;
                user.setIntentosFallidos(intentos);

                if (intentos >= 3) {
                    user.setBloqueado(true);
                    mensaje = "Tu cuenta ha sido bloqueada por múltiples intentos fallidos. Recupera tu contraseña.";
                } else {
                    mensaje = String.format(
                            "La contraseña o el usuario no son correctos. Intento %d de 3.",
                            intentos
                    );
                }

                userRepository.save(user);
            } else {
                mensaje = "La contraseña o el usuario no son correctos.";
            }
        } else {
            mensaje = "Error de autenticación.";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + mensaje + "\"}");
    }
}
