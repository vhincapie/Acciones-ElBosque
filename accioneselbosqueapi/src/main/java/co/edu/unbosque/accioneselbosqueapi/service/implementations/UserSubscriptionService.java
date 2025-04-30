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

    public UserSubscriptionService(IUserService userService, IUserSubscriptionRepository subscriptionRepository, IUserRepository userRepository, ModelMapper modelMapper) {
        this.userService = userService;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public String crearSesionCheckout(String tipoPlan) throws StripeException {
        User user = userService.getAuthenticatedUserEntity();

        UserSubscriptionDTO updatedSub = verificarYActualizarEstado();

        if (user.getRole() != Rol.INVERSIONISTA) {
            throw new SuscripcionException("Solo los usuarios con rol INVERSIONISTA pueden adquirir una suscripcion.");
        }
        if (updatedSub != null && updatedSub.isActivo()) {
            throw new SuscripcionException("Ya tienes una suscripcion activa.");
        }

        Optional<UserSubscription> subOpt = subscriptionRepository.findByUser(user);
        if (subOpt.isPresent() && Boolean.TRUE.equals(subOpt.get().isActivo())) {
            throw new SuscripcionException("Ya tienes una suscripcion activa.");
        }

        Stripe.apiKey = stripeSecretKey;
        String priceId = tipoPlan.equalsIgnoreCase("anual") ? priceAnual : priceMensual;

        SessionCreateParams params = SessionCreateParams.builder()
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

        Session session = Session.create(params);
        return session.getUrl();
    }

    @Override
    public UserSubscriptionDTO activarSuscripcionDesdeWebhook(String email, String tipoPlan) {

        Stripe.apiKey = stripeSecretKey;

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SuscripcionException("Usuario no encontrado con ese correo"));

        if (user.getRole() != Rol.INVERSIONISTA) {
            throw new SuscripcionException("Solo los usuarios con rol INVERSIONISTA pueden tener suscripcion.");
        }

        LocalDate fechaInicio;
        LocalDate fechaCancelacion;
        String customerId;
        String subscriptionId;

        try {
            customerId = "cus_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            subscriptionId = "sub_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);

            fechaInicio = LocalDate.now();
            if ("mensual".equalsIgnoreCase(tipoPlan)) {
                fechaCancelacion = fechaInicio.plusMonths(1);
            } else if ("anual".equalsIgnoreCase(tipoPlan)) {
                fechaCancelacion = fechaInicio.plusYears(1);
            } else {
                fechaCancelacion = null;
            }

        } catch (Exception e) {
            throw new SuscripcionException("Error al generar informacion de suscripcion: " + e.getMessage());
        }

        UserSubscriptionDTO dto = new UserSubscriptionDTO();
        dto.setUser(user.getId());
        dto.setStripeCustomerId(customerId);
        dto.setStripeSubscriptionId(subscriptionId);
        dto.setActivo(true);
        dto.setFechaInicio(fechaInicio);
        dto.setFechaCancelacion(fechaCancelacion);
        dto.setTipoPlan(tipoPlan);

        UserSubscription subscription = modelMapper.map(dto, UserSubscription.class);
        subscription.setUser(user);

        Optional<UserSubscription> existente = subscriptionRepository.findByUser(user);
        if (existente.isPresent()) {
            subscription.setId(existente.get().getId());
            System.out.println("Suscripcion existente actualizada para: " + email);
        } else {
            System.out.println("Nueva suscripcion creada para: " + email);
        }

        subscriptionRepository.save(subscription);

        return modelMapper.map(subscription, UserSubscriptionDTO.class);
    }

    @Override
    public UserSubscriptionDTO verificarYActualizarEstado() {
        User user = userService.getAuthenticatedUserEntity();

        Optional<UserSubscription> optionalSub = subscriptionRepository.findByUser(user);
        if (optionalSub.isEmpty()) {
            return null;
        }

        UserSubscription subscription = optionalSub.get();

        if (subscription.isActivo()) {
            LocalDate hoy = LocalDate.now();

            if (subscription.getFechaCancelacion() != null && hoy.isAfter(subscription.getFechaCancelacion())) {
                subscription.setActivo(false);
                subscription.setTipoPlan(null);
                subscription.setStripeCustomerId(null);
                subscription.setStripeSubscriptionId(null);
                subscription.setFechaInicio(null);
                subscription.setFechaCancelacion(null);
                subscriptionRepository.save(subscription);
            }
        }

        return modelMapper.map(subscription, UserSubscriptionDTO.class);
    }

    @Override
    public boolean esUsuarioPremium() {
        UserSubscriptionDTO updatedSubscription = verificarYActualizarEstado();
        return updatedSubscription != null && updatedSubscription.isActivo();
    }


}
