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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
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
        validarDatosRegistro(request);

        MarketInterest mercado = marketInterestRepository.findById(request.getMarketInterestId())
                .orElseThrow(() -> new MarketInterestNotFoundException("Mercado de interes no encontrado"));

        UserDTO userDTO = construirUserDTO(request, mercado);
        completarDatosFaltantes(request);

        AlpacaAccountResponseDTO response;
        try {
            response = alpacaService.crearCuenta(request);
        } catch (Exception e) {
            throw new AlpacaAccountCreationException("No se pudo completar el registro con Alpaca. Intenta mas tarde.");
        }

        User nuevoUsuario = modelMapper.map(userDTO, User.class);
        User savedUser = userRepository.save(nuevoUsuario);

        crearSuscripcionInicial(savedUser);
        alpacaService.guardarCuentaAlpaca(response, savedUser.getId());

        return modelMapper.map(savedUser, UserDTO.class);
    }

    private void validarDatosRegistro(CreateAccountRequestDTO request) {
        if (request.getContact() == null)
            throw new AlpacaBadRequestException("Faltan datos obligatorios.");

        if (request.getPassword() == null || request.getPassword().isBlank())
            throw new AlpacaBadRequestException("La contraseña no puede ser vacía.");

        if (userRepository.existsByEmail(request.getContact().getEmailAddress()))
            throw new AlpacaBadRequestException("Ya existe un usuario con ese correo.");

        LocalDate nacimiento = LocalDate.parse(request.getIdentity().getDateOfBirth());
        if (Period.between(nacimiento, LocalDate.now()).getYears() < 18)
            throw new AlpacaBadRequestException("Debes ser mayor de edad.");
    }

    private UserDTO construirUserDTO(CreateAccountRequestDTO request, MarketInterest mercado) {
        UserDTO dto = new UserDTO();
        dto.setEmail(request.getContact().getEmailAddress());
        dto.setNombre(request.getContact().getGivenName() + " " + request.getContact().getFamilyName());
        dto.setUsername(dto.getEmail());
        dto.setPassword(passwordEncoder.encode(request.getPassword()));
        dto.setRole(Rol.INVERSIONISTA);
        dto.setIntentosFallidos(0);
        dto.setBloqueado(false);
        dto.setMarketInterestDTO(modelMapper.map(mercado, MarketInterestDTO.class));
        return dto;
    }

    private void completarDatosFaltantes(CreateAccountRequestDTO request) {
        request.getIdentity().setTaxId("958-22-7431");
        request.getIdentity().setTaxIdType("USA_SSN");
        request.getIdentity().setFundingSource(List.of("employment_income"));

        DisclosuresDTO disclosures = new DisclosuresDTO();
        disclosures.setControlPerson(false);
        disclosures.setAffiliatedExchangeOrFinra(false);
        disclosures.setPoliticallyExposed(false);
        disclosures.setImmediateFamilyExposed(false);
        request.setDisclosures(disclosures);

        request.setAgreements(List.of(
                new AgreementDTO("account_agreement", LocalDateTime.now(), "192.168.1.2"),
                new AgreementDTO("customer_agreement", LocalDateTime.now(), "192.168.1.2"),
                new AgreementDTO("margin_agreement", LocalDateTime.now(), "192.168.1.2")
        ));

        DocumentDTO doc = new DocumentDTO();
        doc.setDocumentType("identity_verification");
        doc.setContent("/9j/4AAQSkZJRgABAQEASABIAAD/2w==");
        doc.setMimeType("image/jpeg");
        request.setDocuments(List.of(doc));

        request.setEnabledAssets(List.of("us_equity"));

        TrustedContactDTO contacto = new TrustedContactDTO();
        contacto.setGivenName("Laura");
        contacto.setFamilyName("Quintero");
        contacto.setEmailAddress("laura.quintero@test.com");
        request.setTrustedContact(contacto);
    }

    private void crearSuscripcionInicial(User user) {
        UserSubscriptionDTO subDto = new UserSubscriptionDTO();
        subDto.setUser(user.getId());
        subDto.setActivo(false);
        UserSubscription sub = modelMapper.map(subDto, UserSubscription.class);
        sub.setUser(user);
        subscriptionRepository.save(sub);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        Authentication auth = autenticar(request.getUsername(), request.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new LoginResponseDTO(user.getId(), user.getUsername(), user.getNombre());
    }

    @Override
    public Authentication autenticar(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (user.isBloqueado())
            throw new CuentaBloqueadaException("Tu cuenta esta bloqueada. Recupera tu contraseña.");

        if (!passwordEncoder.matches(password, user.getPassword())) {
            manejarIntentosFallidos(user, false);
            throw new BadCredentialsException("Usuario o contraseña invalidos");
        }

        manejarIntentosFallidos(user, true);

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    private void manejarIntentosFallidos(User user, boolean exitoso) {
        if (exitoso) {
            user.setIntentosFallidos(0);
            user.setBloqueado(false);
        } else {
            user.setIntentosFallidos(user.getIntentosFallidos() + 1);
            if (user.getIntentosFallidos() >= 3) user.setBloqueado(true);
        }
        userRepository.save(user);
    }

    @Override
    public void sendRecoveryEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNoExisteException("No existe una cuenta registrada con ese correo."));

        user.setResetToken(UUID.randomUUID().toString());
        userRepository.save(user);

        emailService.enviarCorreo(email, "Recuperacion de contraseña", construirCuerpoRecuperacion(user));
    }

    private String construirCuerpoRecuperacion(User user) {
        String link = "http://localhost:5173/reset-password?token=" + user.getResetToken();
        return "Hola " + user.getNombre() + ",\n\n" +
                "Solicitaste restablecer tu contraseña. Usa este enlace para continuar:\n" +
                link + "\n\nSi no fuiste tú, ignora este mensaje.";
    }

    @Override
    public UserDTO resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new TokenRecuperacionInvalidoException("Token invalido o expirado"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setIntentosFallidos(0);
        user.setBloqueado(false);

        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (user.isBloqueado())
            throw new RuntimeException("Tu cuenta esta bloqueada. Recupera tu contraseña.");

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }

    @Override
    public User getAuthenticatedUserEntity() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
    }

    @Override
    public UsuarioPanelResponseDTO getPanelInfo() {
        User user = getAuthenticatedUserEntity();

        UsuarioPanelResponseDTO dto = new UsuarioPanelResponseDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setNombre(user.getNombre());
        dto.setRol(user.getRole() != null ? user.getRole().name() : "Sin rol");
        dto.setMarketInterest(user.getMercadoInteres() != null ? user.getMercadoInteres().getNombre() : "Sin mercado asignado");

        Optional<UserSubscription> sub = subscriptionRepository.findByUser(user);

        if (sub.isPresent()) {
            dto.setActivo(sub.get().isActivo());
            dto.setTipoPlan(sub.get().getTipoPlan() != null ? sub.get().getTipoPlan() : "Sin tipo");
            dto.setCustomer(sub.get().getStripeCustomerId() != null ? sub.get().getStripeCustomerId() : "Sin cliente");
            dto.setSubscription(sub.get().getStripeSubscriptionId() != null ? sub.get().getStripeSubscriptionId() : "Sin suscripcion");
        } else {
            dto.setActivo(false);
            dto.setTipoPlan("Sin suscripcion");
            dto.setCustomer("No aplica");
            dto.setSubscription("No aplica");
        }

        return dto;
    }
}
