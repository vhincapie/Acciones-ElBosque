package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.UsuarioNoEncontradoException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import co.edu.unbosque.accioneselbosqueapi.model.entity.MarketInterest;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IMarketInterestRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserSubscriptionRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAlpacaService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private IUserRepository userRepository;
    private IUserSubscriptionRepository subscriptionRepository;
    private IMarketInterestRepository marketInterestRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private ModelMapper modelMapper;
    private IAlpacaService alpacaService;
    private IUserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(IUserRepository.class);
        subscriptionRepository = mock(IUserSubscriptionRepository.class);
        marketInterestRepository = mock(IMarketInterestRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        emailService = mock(EmailService.class);
        modelMapper = new ModelMapper();
        alpacaService = mock(IAlpacaService.class);

        userService = new UserService(
                userRepository,
                subscriptionRepository,
                marketInterestRepository,
                passwordEncoder,
                emailService,
                modelMapper,
                alpacaService
        );
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerDeberiaCrearUsuarioCorrectamente() {
        // Arrange
        CreateAccountRequestDTO request = new CreateAccountRequestDTO();
        ContactDTO contact = new ContactDTO();
        contact.setEmailAddress("test@correo.com");
        contact.setGivenName("Ana");
        contact.setFamilyName("Gómez");
        request.setContact(contact);
        request.setPassword("clave123");

        IdentityDTO identity = new IdentityDTO();
        identity.setDateOfBirth("2000-01-01");
        request.setIdentity(identity);
        request.setMarketInterestId(1L);

        MarketInterest mercado = new MarketInterest();
        mercado.setId(1L);
        mercado.setNombre("Acciones");

        when(marketInterestRepository.findById(1L)).thenReturn(Optional.of(mercado));
        when(userRepository.existsByEmail("test@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("clave123")).thenReturn("encoded123");

        AlpacaAccountResponseDTO alpacaAccount = new AlpacaAccountResponseDTO();
        when(alpacaService.crearCuenta(request)).thenReturn(alpacaAccount);

        User userEntity = new User();
        userEntity.setId(100L);
        userEntity.setUsername("test@correo.com");
        when(userRepository.save(any(User.class))).thenReturn(userEntity);

        // Act
        UserDTO result = userService.register(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("test@correo.com");
        verify(userRepository).save(any(User.class));
        verify(alpacaService).guardarCuentaAlpaca(eq(alpacaAccount), eq(100L));
        verify(subscriptionRepository).save(any());
    }

    @Test
    void getAuthenticatedUserEntityDeberiaRetornarUsuarioCuandoAutenticado() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario123");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setUsername("usuario123");

        when(userRepository.findByUsername("usuario123")).thenReturn(Optional.of(user));

        // Act
        User result = userService.getAuthenticatedUserEntity();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("usuario123");
    }

    @Test
    void getAuthenticatedUserEntityDeberiaLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuarioInexistente");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("usuarioInexistente")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsuarioNoEncontradoException.class, () -> userService.getAuthenticatedUserEntity());
    }

    @Test
    void resetPasswordDeberiaActualizarContrasenaYResetearToken() {
        // Arrange
        String token = UUID.randomUUID().toString();
        String nuevaClave = "NuevaClave123";

        User user = new User();
        user.setUsername("reset@test.com");
        user.setResetToken(token);
        user.setIntentosFallidos(3);
        user.setBloqueado(true);

        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(nuevaClave)).thenReturn("encodedNew");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserDTO result = userService.resetPassword(token, nuevaClave);

        // Assert
        assertThat(result.getUsername()).isEqualTo("reset@test.com");
        assertThat(user.getIntentosFallidos()).isZero();
        assertThat(user.isBloqueado()).isFalse();
    }

    @Test
    void sendRecoveryEmailDeberiaEnviarCorreoConToken() {
        // Arrange
        String email = "correo@prueba.com";
        User user = new User();
        user.setEmail(email);
        user.setNombre("Carlos");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        userService.sendRecoveryEmail(email);

        // Assert
        assertThat(user.getResetToken()).isNotNull();
        verify(emailService).enviarCorreo(eq(email), anyString(), contains("http://localhost:5173/reset-password?token="));
    }
}
