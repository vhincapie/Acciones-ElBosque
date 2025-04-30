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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        if (request.getContact() == null ) {
            throw new AlpacaBadRequestException("Faltan datos obligatorios para crear la cuenta.");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AlpacaBadRequestException("La contraseña no puede ser nula o vacia");
        }

        // Validar si ya existe el usuario
        if (userRepository.existsByEmail(request.getContact().getEmailAddress())) {
            throw new AlpacaBadRequestException("Ya existe un usuario con ese correo electronico.");
        }

        //Validar edad del usuario
        LocalDate fechaNacimiento = LocalDate.parse(request.getIdentity().getDateOfBirth());
        LocalDate fechaActual = LocalDate.now();
        Period edad = Period.between(fechaNacimiento, fechaActual);
        if (edad.getYears() < 18) {
            throw new AlpacaBadRequestException("Debes ser mayor de edad (minimo 18 años) para registrarte.");
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
                .orElseThrow(() -> new MarketInterestNotFoundException("Mercado de interes no encontrado"));
        MarketInterestDTO marketInterestDTO = modelMapper.map(mercado, MarketInterestDTO.class);
        userDTO.setMarketInterestDTO(marketInterestDTO);

        // Setear manualmente todos los datos

        // Identity
        IdentityDTO identityDTO = request.getIdentity();
        identityDTO.setTaxId("958-22-7431");
        identityDTO.setTaxIdType("USA_SSN");
        identityDTO.setFundingSource(List.of("employment_income"));
        request.setIdentity(identityDTO);

        // Disclosures
        DisclosuresDTO disclosuresDTO = new DisclosuresDTO();
        disclosuresDTO.setControlPerson(false);
        disclosuresDTO.setAffiliatedExchangeOrFinra(false);
        disclosuresDTO.setPoliticallyExposed(false);
        disclosuresDTO.setImmediateFamilyExposed(false);
        request.setDisclosures(disclosuresDTO);

        // Agreements
        List<AgreementDTO> agreements = List.of(
                new AgreementDTO("account_agreement", LocalDateTime.now(), "192.168.1.2"),
                new AgreementDTO("customer_agreement", LocalDateTime.now(), "192.168.1.2"),
                new AgreementDTO("margin_agreement", LocalDateTime.now(), "192.168.1.2")
        );
        request.setAgreements(agreements);

        // Documents
        DocumentDTO documentoIdentidad = new DocumentDTO();
        documentoIdentidad.setDocumentType("identity_verification");
        documentoIdentidad.setDocumentSubType(null);
        documentoIdentidad.setContent("/9j/4AAQSkZJRgABAQEASABIAAD/2w==");
        documentoIdentidad.setMimeType("image/jpeg");
        request.setDocuments(List.of(documentoIdentidad));

        // Enabled Assets
        request.setEnabledAssets(List.of("us_equity"));

        // Trusted Contact
        TrustedContactDTO trustedContactDTO = new TrustedContactDTO();
        trustedContactDTO.setGivenName("Laura");
        trustedContactDTO.setFamilyName("Quintero");
        trustedContactDTO.setEmailAddress("laura.quintero@test.com");
        request.setTrustedContact(trustedContactDTO);

        // Intentar crear cuenta en Alpaca primero
        AlpacaAccountResponseDTO response;
        try {
            response = alpacaService.crearCuenta(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AlpacaAccountCreationException("No se pudo completar el registro con Alpaca. Intenta mas tarde.");
        }

        // Guardar usuario solo si se creo bien en Alpaca
        User nuevoUsuario = modelMapper.map(userDTO, User.class);
        User savedUser = userRepository.save(nuevoUsuario);

        // Crear suscripción desactivada inicialmente
        UserSubscriptionDTO subDto = new UserSubscriptionDTO();
        subDto.setUser(savedUser.getId());
        subDto.setActivo(false);
        UserSubscription sub = modelMapper.map(subDto, UserSubscription.class);
        sub.setUser(savedUser);
        subscriptionRepository.save(sub);

        // Guardar cuenta Alpaca en BD
        alpacaService.guardarCuentaAlpaca(response, savedUser.getId());

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        String username = request.getUsername();
        String password = request.getPassword();

        Authentication autenticacion = this.autenticar(username, password);
        SecurityContextHolder.getContext().setAuthentication(autenticacion);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return new LoginResponseDTO(user.getId(), user.getUsername(), user.getNombre());
    }

    @Override
    public Authentication autenticar(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (user.isBloqueado()) {
            throw new CuentaBloqueadaException("Tu cuenta esta bloqueada. Recupera tu contraseña.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            user.setIntentosFallidos(user.getIntentosFallidos() + 1);

            if (user.getIntentosFallidos() >= 3) {
                user.setBloqueado(true);
            }

            userRepository.save(user);

            throw new BadCredentialsException("Usuario o contraseña invalidos");
        }


        if (user.getIntentosFallidos() != 0 || user.isBloqueado()) {
            user.setIntentosFallidos(0);
            user.setBloqueado(false);
            userRepository.save(user);
        }

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
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
                .orElseThrow(() -> new TokenRecuperacionInvalidoException("Token invalido o expirado"));

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
            throw new RuntimeException("Tu cuenta esta bloqueada. Recupera tu contraseña.");
        }

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

        UsuarioPanelResponseDTO response = new UsuarioPanelResponseDTO();
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setNombre(user.getNombre());
        response.setRol(user.getRole() != null ? user.getRole().name() : "Sin rol");
        response.setMarketInterest(user.getMercadoInteres() != null ? user.getMercadoInteres().getNombre() : "Sin mercado asignado");

        Optional<UserSubscription> optionalSub = subscriptionRepository.findByUser(user);

        if (optionalSub.isPresent()) {
            UserSubscription subscription = optionalSub.get();
            response.setActivo(subscription.isActivo());
            response.setTipoPlan(subscription.getTipoPlan() != null ? subscription.getTipoPlan() : "Sin tipo");
            response.setCustomer(subscription.getStripeCustomerId() != null ? subscription.getStripeCustomerId() : "Sin cliente");
            response.setSubscription(subscription.getStripeSubscriptionId() != null ? subscription.getStripeSubscriptionId() : "Sin suscripcion");
        } else {
            response.setActivo(false);
            response.setTipoPlan("Sin suscripcion");
            response.setCustomer("No aplica");
            response.setSubscription("No aplica");
        }

        return response;
    }



}
