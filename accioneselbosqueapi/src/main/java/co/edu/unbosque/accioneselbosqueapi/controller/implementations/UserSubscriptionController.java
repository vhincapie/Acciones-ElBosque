package co.edu.unbosque.accioneselbosqueapi.controller.implementations;

import co.edu.unbosque.accioneselbosqueapi.controller.interfaces.IUserSubscriptionAPI;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SuscripcionException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.CheckoutResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserSubscriptionDTO;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserSubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserSubscriptionController implements IUserSubscriptionAPI {

    private final IUserSubscriptionService subscriptionService;

    public UserSubscriptionController(IUserSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PreAuthorize("hasAnyRole('INVERSIONISTA')")
    @Override
    public ResponseEntity<CheckoutResponseDTO> crearSesionCheckout(String tipoPlan) {
        try {
            String url = subscriptionService.crearSesionCheckout(tipoPlan);
            return ResponseEntity.ok(new CheckoutResponseDTO(url));
        } catch (SuscripcionException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CheckoutResponseDTO(null, ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CheckoutResponseDTO(null, "Error al crear la sesión de pago."));
        }
    }

    @Override
    public ResponseEntity<Boolean> obtenerEstado() {
        boolean estado = subscriptionService.esUsuarioPremium();
        return ResponseEntity.ok(estado);
    }

    @Override
    public ResponseEntity<UserSubscriptionDTO> verificarPremium() {
        UserSubscriptionDTO dto = subscriptionService.verificarYActualizarEstado();
        return ResponseEntity.ok(dto);
    }
}
