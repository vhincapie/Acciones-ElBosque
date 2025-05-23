package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.configurations.ModelMapperConfiguration;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SuscripcionException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserSubscriptionDTO;
import co.edu.unbosque.accioneselbosqueapi.model.Rol;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.model.entity.UserSubscription;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserSubscriptionRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSubscriptionServiceTest {

    private IUserService userService;
    private IUserSubscriptionRepository subscriptionRepository;
    private IUserRepository userRepository;
    private ModelMapper modelMapper;
    private UserSubscriptionService service;

    @BeforeEach
    void setUp() {
        userService = mock(IUserService.class);
        subscriptionRepository = mock(IUserSubscriptionRepository.class);
        userRepository = mock(IUserRepository.class);
        modelMapper = new ModelMapperConfiguration().modelMapper();

        service = new UserSubscriptionService(userService, subscriptionRepository, userRepository, modelMapper);
    }

    @Test
    void verificarYActualizarEstadoDebeRetornarNullSiNoExisteSubscripcion() {
        // Arrange
        User user = new User();
        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.empty());

        // Act
        UserSubscriptionDTO result = service.verificarYActualizarEstado();

        // Assert
        assertNull(result);
    }

    @Test
    void verificarYActualizarEstadoDebeLimpiarSubscripcionVencida() {
        // Arrange
        User user = new User();
        UserSubscription sub = new UserSubscription();
        sub.setActivo(true);
        sub.setFechaCancelacion(LocalDate.now().minusDays(1));
        sub.setUser(user);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(sub));

        // Act
        UserSubscriptionDTO result = service.verificarYActualizarEstado();

        // Assert
        assertFalse(sub.isActivo());
        assertNull(sub.getTipoPlan());
        assertNull(sub.getFechaInicio());
        assertNull(sub.getFechaCancelacion());
        assertNull(sub.getStripeCustomerId());
        assertNull(sub.getStripeSubscriptionId());
        assertThat(result).isNotNull();
    }

    @Test
    void verificarYActualizarEstadoDebeRetornarSubscripcionActivaSinCambios() {
        // Arrange
        User user = new User();
        UserSubscription sub = new UserSubscription();
        sub.setActivo(true);
        sub.setFechaCancelacion(LocalDate.now().plusDays(5));
        sub.setUser(user);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(sub));

        // Act
        UserSubscriptionDTO result = service.verificarYActualizarEstado();

        // Assert
        assertThat(result).isNotNull();
        assertTrue(sub.isActivo());
    }

    @Test
    void esUsuarioPremiumDebeRetornarTrueSiSubscripcionActiva() {
        // Arrange
        User user = new User();
        UserSubscription sub = new UserSubscription();
        sub.setActivo(true);
        sub.setFechaCancelacion(LocalDate.now().plusDays(5));
        sub.setUser(user);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(sub));

        // Act
        boolean premium = service.esUsuarioPremium();

        // Assert
        assertTrue(premium);
    }

    @Test
    void esUsuarioPremiumDebeRetornarFalseSiNoTieneSubscripcion() {
        // Arrange
        User user = new User();
        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.empty());

        // Act
        boolean premium = service.esUsuarioPremium();

        // Assert
        assertFalse(premium);
    }

    @Test
    void activarSuscripcionDesdeWebhookDebeCrearONActualizarSubscripcion() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole(Rol.INVERSIONISTA);
        user.setEmail("test@correo.com");

        when(userRepository.findByEmail("test@correo.com")).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(UserSubscription.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        UserSubscriptionDTO result = service.activarSuscripcionDesdeWebhook("test@correo.com", "mensual");

        // Assert
        assertThat(result).isNotNull();
        assertEquals("mensual", result.getTipoPlan());
        assertTrue(result.isActivo());
    }

    @Test
    void activarSuscripcionDesdeWebhookDebeLanzarExcepcionSiNoEsInversionista() {
        // Arrange
        User user = new User();
        user.setRole(Rol.ADMINISTRADOR);

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(SuscripcionException.class, () -> service.activarSuscripcionDesdeWebhook("admin@test.com", "mensual"));
    }

    @Test
    void activarSuscripcionDesdeWebhookDebeActualizarSubscripcionExistente() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole(Rol.INVERSIONISTA);
        user.setEmail("update@correo.com");

        UserSubscription existing = new UserSubscription();
        existing.setId(99L);
        existing.setUser(user);

        when(userRepository.findByEmail("update@correo.com")).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(UserSubscription.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        UserSubscriptionDTO result = service.activarSuscripcionDesdeWebhook("update@correo.com", "anual");

        // Assert
        assertThat(result).isNotNull();
        assertEquals("anual", result.getTipoPlan());
        assertEquals(99L, result.getId());
    }
}