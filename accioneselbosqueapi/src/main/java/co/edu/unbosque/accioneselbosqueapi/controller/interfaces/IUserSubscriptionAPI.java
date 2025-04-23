package co.edu.unbosque.accioneselbosqueapi.controller.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.CheckoutResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserSubscriptionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/suscripcion")
public interface IUserSubscriptionAPI {

    @GetMapping("/checkout")
    ResponseEntity<CheckoutResponseDTO> crearSesionCheckout(@RequestParam String tipoPlan);

    @GetMapping("/status")
    ResponseEntity<Boolean> obtenerEstado();

    @GetMapping("/verificar-premium")
    ResponseEntity<UserSubscriptionDTO> verificarPremium();


   }
