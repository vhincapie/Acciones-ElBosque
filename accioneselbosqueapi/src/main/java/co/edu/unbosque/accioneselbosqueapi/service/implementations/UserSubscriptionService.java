package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SuscripcionException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserSubscriptionDTO;
import co.edu.unbosque.accioneselbosqueapi.model.Rol;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.model.entity.UserSubscription;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserSubscriptionRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserSubscriptionService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserSubscriptionService implements IUserSubscriptionService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.price.monthly}")
    private String priceMensual;

    @Value("${stripe.price.annual}")
    private String priceAnual;

    private final IUserService userService;
    private final IUserSubscriptionRepository subscriptionRepository;
    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserSubscriptionService(IUserService userService,
                                   IUserSubscriptionRepository subscriptionRepository,
                                   IUserRepository userRepository,
                                   ModelMapper modelMapper) {
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public String crearSesionCheckout(String tipoPlan) throws StripeException {
        User user = userService.getAuthenticatedUserEntity();
        UserSubscriptionDTO updatedSub = verificarYActualizarEstado();

        validarSuscriptor(user, updatedSub);

        String priceId = obtenerPriceIdPorTipo(tipoPlan);
        SessionCreateParams params = construirParametrosCheckout(user, priceId, tipoPlan);

        Stripe.apiKey = stripeSecretKey;
        Session session = Session.create(params);
        return session.getUrl();
    }

    @Override
    public UserSubscriptionDTO activarSuscripcionDesdeWebhook(String email, String tipoPlan) {
        Stripe.apiKey = stripeSecretKey;
        User user = obtenerUsuarioPorEmail(email);
        validarRolInversionista(user);

        UserSubscriptionDTO dto = construirDTO(user, tipoPlan);
        UserSubscription subscription = modelMapper.map(dto, UserSubscription.class);
        subscription.setUser(user);

        subscriptionRepository.findByUser(user).ifPresent(existing -> subscription.setId(existing.getId()));
        subscriptionRepository.save(subscription);

        return modelMapper.map(subscription, UserSubscriptionDTO.class);
    }

    @Override
    public UserSubscriptionDTO verificarYActualizarEstado() {
        User user = userService.getAuthenticatedUserEntity();
        Optional<UserSubscription> optionalSub = subscriptionRepository.findByUser(user);

        if (optionalSub.isEmpty()) return null;

        UserSubscription sub = optionalSub.get();
        if (sub.isActivo() && LocalDate.now().isAfter(sub.getFechaCancelacion())) {
            limpiarSuscripcionVencida(sub);
            subscriptionRepository.save(sub);
        }

        return modelMapper.map(sub, UserSubscriptionDTO.class);
    }

    @Override
    public boolean esUsuarioPremium() {
        UserSubscriptionDTO updatedSubscription = verificarYActualizarEstado();
        return updatedSubscription != null && updatedSubscription.isActivo();
    }

    private void validarSuscriptor(User user, UserSubscriptionDTO subDTO) {
        if (user.getRole() != Rol.INVERSIONISTA) {
            throw new SuscripcionException("Solo los usuarios con rol INVERSIONISTA pueden adquirir una suscripcion.");
        }
        if (subDTO != null && subDTO.isActivo()) {
            throw new SuscripcionException("Ya tienes una suscripcion activa.");
        }

        subscriptionRepository.findByUser(user)
                .filter(UserSubscription::isActivo)
                .ifPresent(s -> {
                    throw new SuscripcionException("Ya tienes una suscripcion activa.");
                });
    }

    private String obtenerPriceIdPorTipo(String tipoPlan) {
        if ("anual".equalsIgnoreCase(tipoPlan)) return priceAnual;
        return priceMensual;
    }

    private SessionCreateParams construirParametrosCheckout(User user, String priceId, String tipoPlan) {
        return SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("http://localhost:5173/success")
                .setCancelUrl("http://localhost:5173/cancel")
                .setCustomerEmail(user.getEmail())
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(priceId)
                        .setQuantity(1L)
                        .build())
                .putMetadata("tipo_plan", tipoPlan.toLowerCase())
                .build();
    }

    private User obtenerUsuarioPorEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new SuscripcionException("Usuario no encontrado con ese correo"));
    }

    private void validarRolInversionista(User user) {
        if (user.getRole() != Rol.INVERSIONISTA) {
            throw new SuscripcionException("Solo los usuarios con rol INVERSIONISTA pueden tener suscripcion.");
        }
    }

    private UserSubscriptionDTO construirDTO(User user, String tipoPlan) {
        LocalDate inicio = LocalDate.now();
        LocalDate cancelacion = calcularFechaCancelacion(inicio, tipoPlan);

        UserSubscriptionDTO dto = new UserSubscriptionDTO();
        dto.setUser(user.getId());
        dto.setStripeCustomerId(generarId("cus"));
        dto.setStripeSubscriptionId(generarId("sub"));
        dto.setActivo(true);
        dto.setFechaInicio(inicio);
        dto.setFechaCancelacion(cancelacion);
        dto.setTipoPlan(tipoPlan);
        return dto;
    }

    private LocalDate calcularFechaCancelacion(LocalDate inicio, String tipoPlan) {
        if ("mensual".equalsIgnoreCase(tipoPlan)) return inicio.plusMonths(1);
        if ("anual".equalsIgnoreCase(tipoPlan)) return inicio.plusYears(1);
        return null;
    }

    private String generarId(String prefijo) {
        return prefijo + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    private void limpiarSuscripcionVencida(UserSubscription sub) {
        sub.setActivo(false);
        sub.setTipoPlan(null);
        sub.setStripeCustomerId(null);
        sub.setStripeSubscriptionId(null);
        sub.setFechaInicio(null);
        sub.setFechaCancelacion(null);
    }
}
