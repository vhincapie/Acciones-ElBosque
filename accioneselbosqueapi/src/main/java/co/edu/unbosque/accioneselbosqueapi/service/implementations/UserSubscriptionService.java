package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SuscripcionException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserDTO;
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
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

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
            throw new SuscripcionException("Solo los usuarios con rol INVERSIONISTA pueden adquirir una suscripción.");
        }
        if (updatedSub != null && updatedSub.isActivo()) {
            throw new SuscripcionException("Ya tienes una suscripción activa.");
        }

        Optional<UserSubscription> subOpt = subscriptionRepository.findByUser(user);
        if (subOpt.isPresent() && Boolean.TRUE.equals(subOpt.get().isActivo())) {
            throw new SuscripcionException("Ya tienes una suscripción activa.");
        }

        Stripe.apiKey = stripeSecretKey;
        String priceId = tipoPlan.equalsIgnoreCase("anual") ? priceAnual : priceMensual;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl("http://localhost:3000/success")
                .setCancelUrl("http://localhost:3000/cancel")
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
    public UserSubscriptionDTO activarSuscripcionDesdeWebhook(String email, String customerId, String subscriptionId, String tipoPlan) {

        Stripe.apiKey = stripeSecretKey;

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new SuscripcionException("Usuario no encontrado con ese correo"));

        if (user.getRole() != Rol.INVERSIONISTA) {
            throw new SuscripcionException("Solo los usuarios con rol INVERSIONISTA pueden tener suscripción.");
        }

        LocalDate fechaInicio;
        LocalDate fechaCancelacion;

        try {
            Subscription stripeSubscription = Subscription.retrieve(subscriptionId);
            long startEpoch = stripeSubscription.getCurrentPeriodStart();
            long endEpoch = stripeSubscription.getCurrentPeriodEnd();

            fechaInicio = Instant.ofEpochSecond(startEpoch).atZone(ZoneId.systemDefault()).toLocalDate();
            fechaCancelacion = Instant.ofEpochSecond(endEpoch).atZone(ZoneId.systemDefault()).toLocalDate();

        } catch (StripeException e) {
            System.err.println("Error al consultar Stripe para obtener fechas de la suscripción: " + e.getMessage());

            fechaInicio = LocalDate.now();
            if ("mensual".equalsIgnoreCase(tipoPlan)) {
                fechaCancelacion = fechaInicio.plusMonths(1);
            } else if ("anual".equalsIgnoreCase(tipoPlan)) {
                fechaCancelacion = fechaInicio.plusYears(1);
            } else {
                fechaCancelacion = null;
            }
        }


        UserSubscriptionDTO dto = new UserSubscriptionDTO();
        dto.setUser(modelMapper.map(user, UserDTO.class));
        dto.setStripeCustomerId(customerId);
        dto.setStripeSubscriptionId(subscriptionId);
        dto.setActivo(true);
        dto.setFechaInicio(fechaInicio);
        dto.setFechaCancelacion(fechaCancelacion);


        UserSubscription subscription = modelMapper.map(dto, UserSubscription.class);
        subscription.setUser(user);


        Optional<UserSubscription> existente = subscriptionRepository.findByUser(user);
        if (existente.isPresent()) {
            subscription.setId(existente.get().getId()); // actualiza la existente
            System.out.println("Suscripción existente actualizada para: " + email);
        } else {
            System.out.println("Nueva suscripción creada para: " + email);
        }

        subscriptionRepository.save(subscription);

        return modelMapper.map(subscription, UserSubscriptionDTO.class);
    }

    @Override
    public UserSubscriptionDTO verificarYActualizarEstado() {
        User user = userService.getAuthenticatedUserEntity();
        UserSubscription subscription = user.getSubscription();

        if (subscription != null && subscription.isActivo()) {
            LocalDate hoy = LocalDate.now();

            if (subscription.getFechaCancelacion() != null && hoy.isAfter(subscription.getFechaCancelacion())) {
                UserSubscriptionDTO dto = modelMapper.map(subscription, UserSubscriptionDTO.class);
                dto.setActivo(false);
                subscription = modelMapper.map(dto, UserSubscription.class);
                subscriptionRepository.save(subscription);
                System.out.println("Suscripción desactivada por vencimiento para el usuario: " + user.getEmail());
            }
        }

        return subscription != null ? modelMapper.map(subscription, UserSubscriptionDTO.class) : null;
    }

    @Override
    public boolean esUsuarioPremium() {
        UserSubscriptionDTO updatedSubscription = verificarYActualizarEstado();
        return updatedSubscription != null && updatedSubscription.isActivo();
    }
}
