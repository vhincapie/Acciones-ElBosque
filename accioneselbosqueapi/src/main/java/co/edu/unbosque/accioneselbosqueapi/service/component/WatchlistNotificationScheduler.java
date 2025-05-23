package co.edu.unbosque.accioneselbosqueapi.service.component;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.StockDataUnavailableException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.WatchlistItem;
import co.edu.unbosque.accioneselbosqueapi.repository.IWatchlistRepository;
import co.edu.unbosque.accioneselbosqueapi.service.implementations.EmailService;
import co.edu.unbosque.accioneselbosqueapi.service.implementations.WatchlistService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class WatchlistNotificationScheduler {

    private final IWatchlistRepository watchlistRepository;
    private final WatchlistService watchlistService;
    private final EmailService emailService;

    private final Map<Long, LocalDateTime> alertasEnviadas = new HashMap<>();

    public WatchlistNotificationScheduler(IWatchlistRepository watchlistRepository,
                                          WatchlistService watchlistService,
                                          EmailService emailService) {
        this.watchlistRepository = watchlistRepository;
        this.watchlistService = watchlistService;
        this.emailService = emailService;
    }

    @Scheduled(fixedRate = 10000)
    public void verificarAlertasDePrecios() {
        List<WatchlistItem> items = watchlistRepository.findAll();

        for (WatchlistItem item : items) {
            Double min = item.getAlertMinPrice();
            Double max = item.getAlertMaxPrice();
            if (min == null && max == null) continue;

            try {
                StockDTO data = watchlistService.findBySymbol(item.getSymbol());
                double actual = data.getCurrentPrice();
                boolean fueraDeRango = (min != null && actual < min) || (max != null && actual > max);
                if (fueraDeRango && debeEnviar(item.getId())) {
                    enviarAlertaDePrecio(item, actual, min, max);
                }
            } catch (Exception e) {
                throw new StockDataUnavailableException("Error verificando precios para " + item.getSymbol() + ": " + e.getMessage());
            }
        }
    }

    private boolean debeEnviar(Long itemId) {
        LocalDateTime ultima = alertasEnviadas.get(itemId);
        return ultima == null || Duration.between(ultima, LocalDateTime.now()).toMinutes() >= 60;
    }

    private void enviarAlertaDePrecio(WatchlistItem item, double actual, Double min, Double max) {
        String asunto = "🚨 Alerta de precio para " + item.getSymbol();
        String mensaje = construirMensaje(item.getUser().getNombre(), item.getSymbol(), actual, min, max);
        emailService.enviarCorreo(item.getUser().getEmail(), asunto, mensaje);
        alertasEnviadas.put(item.getId(), LocalDateTime.now());
    }

    private String construirMensaje(String nombre, String symbol, double actual, Double min, Double max) {
        StringBuilder msg = new StringBuilder("Hola " + nombre + ",\n\n");
        msg.append("El precio actual de la acción ").append(symbol).append(" es: $").append(actual).append("\n");
        if (min != null && actual < min) {
            msg.append("Ha caído por debajo del mínimo configurado ($").append(min).append(").\n");
        }
        if (max != null && actual > max) {
            msg.append("Ha superado el máximo configurado ($").append(max).append(").\n");
        }
        msg.append("\nVerifica si deseas realizar alguna acción.\n\n");
        msg.append("Acciones El Bosque");
        return msg.toString();
    }
}

