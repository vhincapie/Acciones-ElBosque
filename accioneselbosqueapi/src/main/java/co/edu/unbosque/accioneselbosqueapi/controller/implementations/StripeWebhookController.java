package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IStripeWebhookAPI;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserSubscriptionDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserSubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StripeWebhookController implements IStripeWebhookAPI {

    private final IUserSubscriptionService subscriptionService;

    public StripeWebhookController(IUserSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @Override
    public ResponseEntity<UserSubscriptionDTO> handleStripeWebhook(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            Map<String, Object> session = (Map<String, Object>) data.get("object");

            String email = (String) session.get("customer_email");
            String customerId = (String) session.get("customer");
            String subscriptionId = (String) session.get("subscription");
            String tipoPlan = (String) session.get("tipo_plan");

            if (email != null && customerId != null && subscriptionId != null && tipoPlan != null) {
                UserSubscriptionDTO dto = subscriptionService.activarSuscripcionDesdeWebhook(email, customerId, subscriptionId, tipoPlan);
                return ResponseEntity.ok(dto);
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
