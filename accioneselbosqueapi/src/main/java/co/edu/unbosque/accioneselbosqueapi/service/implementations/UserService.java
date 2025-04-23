package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.*;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import co.edu.unbosque.accioneselbosqueapi.model.Rol;
import co.edu.unbosque.accioneselbosqueapi.model.entity.MarketInterest;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.model.entity.UserSubscription;
import co.edu.unbosque.accioneselbosqueapi.repository.IMarketInterestRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserSubscriptionRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAlpacaService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService implements IUserService, UserDetailsService {

    private final IUserRepository userRepository;
    private final IUserSubscriptionRepository subscriptionRepository;
    private final IMarketInterestRepository marketInterestRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ModelMapper modelMapper;
    private final IAlpacaService alpacaService;

    public UserService(IUserRepository userRepository,
                       IUserSubscriptionRepository subscriptionRepository,
                       IMarketInterestRepository marketInterestRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       ModelMapper modelMapper,
                       IAlpacaService alpacaService) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.marketInterestRepository = marketInterestRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.alpacaService = alpacaService;
    }

    @Override
    public UserDTO register(CreateAccountRequestDTO request) {
        if (request.getContact() == null || request.getIdentity() == null || request.getDisclosures() == null) {
            throw new AlpacaBadRequestException("Faltan datos obligatorios para crear la cuenta.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AlpacaBadRequestException("La contraseña no puede ser nula o vacía");
        }

        // Validar si ya existe el usuario
        if (userRepository.existsByEmail(request.getContact().getEmailAddress())) {
            throw new AlpacaBadRequestException("Ya existe un usuario con ese correo electrónico.");
        }

        // Construccion del DTO
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(request.getContact().getEmailAddress());
        userDTO.setNombre(request.getContact().getGivenName() + " " + request.getContact().getFamilyName());
        userDTO.setUsername(request.getContact().getEmailAddress());
        userDTO.setPassword(passwordEncoder.encode(request.getPassword()));
        userDTO.setRole(Rol.INVERSIONISTA);
        userDTO.setIntentosFallidos(0);
        userDTO.setBloqueado(false);

        MarketInterest mercado = marketInterestRepository.findById(request.getMarketInterestId())
                .orElseThrow(() -> new MarketInterestNotFoundException("Mercado de interés no encontrado"));
        MarketInterestDTO marketInterestDTO = modelMapper.map(mercado, MarketInterestDTO.class);
        userDTO.setMarketInterestDTO(marketInterestDTO);

        // Establecer acuerdos (simulados)
        List<AgreementDTO> agreements = List.of(
                new AgreementDTO("account_agreement", LocalDateTime.now(), "192.168.1.2"),
                new AgreementDTO("customer_agreement", LocalDateTime.now(), "192.168.1.2"),
                new AgreementDTO("margin_agreement", LocalDateTime.now(), "192.168.1.2")
        );
        request.setAgreements(agreements);

        // Intentar crear cuenta en Alpaca primero
        AlpacaAccountResponseDTO response;
        try {
            response = alpacaService.crearCuenta(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AlpacaAccountCreationException("No se pudo completar el registro con Alpaca. Intenta más tarde.");
        }

        // Guardar usuario solo si se creo bien en Alpaca
        User nuevoUsuario = modelMapper.map(userDTO, User.class);
        User savedUser = userRepository.save(nuevoUsuario);

        // Crear suscripción desactivada inicialmente
        UserSubscriptionDTO subDto = new UserSubscriptionDTO();
        subDto.setUser(userDTO);
        subDto.setActivo(false);
        UserSubscription sub = modelMapper.map(subDto, UserSubscription.class);
        sub.setUser(savedUser);
        subscriptionRepository.save(sub);

        // Guardar cuenta Alpaca en BD
        alpacaService.guardarCuentaAlpaca(response, savedUser.getId());

        return modelMapper.map(savedUser, UserDTO.class);
    }


    @Override
    public void sendRecoveryEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNoExisteException("No existe una cuenta registrada con ese correo."));

        user.setResetToken(UUID.randomUUID().toString());
        userRepository.save(user);

        String link = "http://localhost:8080/reset-password?token=" + user.getResetToken();
        String cuerpo = "Hola " + user.getNombre() + ",\n\n" +
                "Solicitaste restablecer tu contraseña. Usa este enlace para continuar:\n" +
                link + "\n\nSi no fuiste tú, ignora este mensaje.";

        emailService.enviarCorreo(email, "Recuperación de contraseña", cuerpo);
    }

    @Override
    public UserDTO resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new TokenRecuperacionInvalidoException("Token inválido o expirado"));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setPassword(passwordEncoder.encode(newPassword));
        userDTO.setResetToken(null);
        userDTO.setIntentosFallidos(0);
        userDTO.setBloqueado(false);

        User updated = modelMapper.map(userDTO, User.class);
        userRepository.save(updated);

        return modelMapper.map(updated, UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (user.isBloqueado()) {
            throw new RuntimeException("Tu cuenta está bloqueada. Recupera tu contraseña.");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    @Override
    public UserDTO getAuthenticatedUserInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));


        if (user.isBloqueado()) {
            throw new CuentaBloqueadaException("Tu cuenta está bloqueada. Recupera tu contraseña.");
        }

        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public User getAuthenticatedUserEntity() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }
}
