package co.edu.unbosque.accioneselbosqueapi.service.component;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.MarketStatusDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.service.implementations.EmailService;
import co.edu.unbosque.accioneselbosqueapi.service.implementations.MarketService;
import co.edu.unbosque.accioneselbosqueapi.service.implementations.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MarketNotificationScheduler {

    private final MarketService marketService;
    private final EmailService emailService;
    private final UserService userService;

    private Boolean estadoAnterior = null;

    public MarketNotificationScheduler(MarketService marketService, EmailService emailService, UserService userService) {
        this.marketService = marketService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @Scheduled(fixedRate = 10000)
    public void verificarEstadoMercado() {
        MarketStatusDTO estadoActual = marketService.getMarketStatus();

        if (estadoAnterior == null || estadoAnterior != estadoActual.isAbierto()) {
            estadoAnterior = estadoActual.isAbierto();
            notificarCambioDeMercado(estadoActual);
        }
    }

    private void notificarCambioDeMercado(MarketStatusDTO estadoActual) {
        String asunto = "Estado del mercado";
        for (User user : userService.obtenerTodosLosUsuariosInversionistas()) {
            String cuerpo = "Hola " + user.getNombre() + ",\n\n" +
                    "Te informamos que " + estadoActual.getMensaje() + ".\n\n" +
                    "Saludos,\nAcciones El Bosque";
            emailService.enviarCorreo(user.getEmail(), asunto, cuerpo);
        }
    }
}
