package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.*;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import co.edu.unbosque.accioneselbosqueapi.model.Rol;
import co.edu.unbosque.accioneselbosqueapi.model.entity.MarketInterest;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAlpacaService;
import co.edu.unbosque.accioneselbosqueapi.repository.IMarketInterestRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements IUserService, UserDetailsService {

    private final IUserRepository userRepository;
    private final IMarketInterestRepository marketInterestRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final IAlpacaService alpacaService;


    public UserService(IUserRepository userRepository, IMarketInterestRepository marketInterestRepository, PasswordEncoder passwordEncoder,
                       EmailService emailService, ModelMapper modelMapper, IAlpacaService alpacaService) {
        this.userRepository = userRepository;
        this.marketInterestRepository = marketInterestRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.alpacaService = alpacaService;
    }


    @Override
    public void register(CreateAccountRequestDTO request) {
        if (request.getContact() == null || request.getIdentity() == null || request.getDisclosures() == null) {
            throw new AlpacaBadRequestException("Faltan datos obligatorios para crear la cuenta.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AlpacaBadRequestException("La contraseña no puede ser nula o vacía");
        }

        User nuevoUsuario = new User();
        nuevoUsuario.setEmail(request.getContact().getEmailAddress());
        nuevoUsuario.setNombre(request.getContact().getGivenName() + " " + request.getContact().getFamilyName());
        nuevoUsuario.setUsername(request.getContact().getEmailAddress());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setRole(Rol.INVERSIONISTA);
        nuevoUsuario.setIntentosFallidos(0);
        nuevoUsuario.setBloqueado(false);

        MarketInterest mercadoInteres = marketInterestRepository.findById(request.getMarketInterestId())
                .orElseThrow(() -> new MarketInterestNotFoundException("Mercado de interés no encontrado"));
        nuevoUsuario.setMercadoInteres(mercadoInteres);

        userRepository.save(nuevoUsuario);

        List<AgreementDTO> agreements = List.of(
                new AgreementDTO("account_agreement", LocalDateTime.now(), "192.168.1.2"),
                new AgreementDTO("customer_agreement", LocalDateTime.now(), "192.168.1.2"),
                new AgreementDTO("margin_agreement", LocalDateTime.now(), "192.168.1.2")
        );
        request.setAgreements(agreements);

        try {
            AlpacaAccountResponseDTO response = alpacaService.crearCuenta(request);
            alpacaService.guardarCuentaAlpaca(response, nuevoUsuario.getId());
            System.out.println("Usuario y cuenta creados con éxito");
        } catch (Exception e) {
            System.err.println("Error en la creación de la cuenta Alpaca: " + e.getMessage());
            throw new AlpacaAccountCreationException("No se pudo completar el registro con Alpaca. Intenta más tarde.");
        }
    }


    @Override
    public void sendRecoveryEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNoExisteException("No existe una cuenta registrada con ese correo."));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        String link = "http://localhost:8080/reset-password?token=" + token;
        String asunto = "Recuperación de contraseña";
        String cuerpo = "Hola " + user.getNombre() + ",\n\n" +
                "Solicitaste restablecer tu contraseña. Usa este enlace para continuar:\n" +
                link + "\n\n" +
                "Si no fuiste tú, ignora este mensaje.";

        emailService.enviarCorreo(email, asunto, cuerpo);
    }


    @Override
    public void resetPassword(String token, String newPassword) {
        Optional<User> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            throw new TokenRecuperacionInvalidoException("El token de recuperación es inválido o ha expirado.");
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setIntentosFallidos(0);
        user.setBloqueado(false);

        System.out.println("Contraseña reseteada para usuario: " + user.getUsername());
        System.out.println("Estado bloqueado: " + user.isBloqueado());
        System.out.println("Intentos fallidos: " + user.getIntentosFallidos());


        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (user.isBloqueado()) {
            throw new RuntimeException("Tu cuenta esta bloqueada. Recupera tu contraseña.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );


    }

    @Override
    public UserDTO getAuthenticatedUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        if (user.isBloqueado()) {
            throw new CuentaBloqueadaException("Tu cuenta esta bloqueada. Recupera tu contraseña.");
        }

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public User getAuthenticatedUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }


}
