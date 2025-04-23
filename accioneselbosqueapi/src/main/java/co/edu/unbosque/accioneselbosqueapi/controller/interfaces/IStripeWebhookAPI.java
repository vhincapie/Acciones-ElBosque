package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserSubscriptionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/stripe")
public interface IStripeWebhookAPI {

    @PostMapping("/webhook")
    ResponseEntity<UserSubscriptionDTO> handleStripeWebhook(@RequestBody Map<String, Object> payload);


   }
