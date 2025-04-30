package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserSubscriptionDTO;
import com.stripe.exception.StripeException;

public interface IUserSubscriptionService {

    String crearSesionCheckout(String tipoPlan) throws StripeException;
    UserSubscriptionDTO activarSuscripcionDesdeWebhook(String email, String tipoPlan);
    UserSubscriptionDTO verificarYActualizarEstado();
    boolean esUsuarioPremium();

}