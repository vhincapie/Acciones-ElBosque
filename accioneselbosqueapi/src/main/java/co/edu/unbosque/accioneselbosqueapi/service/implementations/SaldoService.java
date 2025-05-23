package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SaldoException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.TradingDetailDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ISaldoService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@Transactional
public class SaldoService implements ISaldoService {

    private static final Logger logger = LoggerFactory.getLogger(SaldoService.class);

    private final IUserService userService;
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final RestTemplate restTemplate;

    @Value("${alpaca.broker.account-status-url}")
    private String baseUrl;

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    public SaldoService(IUserService userService, IAlpacaAccountRepository alpacaAccountRepository, RestTemplate restTemplate) {
        this.userService = userService;
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public TradingDetailDTO obtenerSaldoUsuarioActual() {
        User user = userService.getAuthenticatedUserEntity();

        AlpacaAccount alpacaAccount = alpacaAccountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new SaldoException("No se encontro cuenta Alpaca asociada al usuario."));

        String alpacaId = alpacaAccount.getAlpacaId();
        TradingDetailDTO tradingDetail = obtenerDetallesCuentaDesdeAlpaca(alpacaId);

        try {
            BigDecimal buyingPower = new BigDecimal(tradingDetail.getBuyingPower());
            alpacaAccount.setBuyingPower(buyingPower);
            alpacaAccountRepository.save(alpacaAccount);
        } catch (NumberFormatException e) {
            throw new SaldoException("No se pudo convertir el buying power a formato numerico.");
        }

        return tradingDetail;
    }


    private TradingDetailDTO obtenerDetallesCuentaDesdeAlpaca(String alpacaId) {
        String url = baseUrl + "/" + alpacaId + "/account";
        logger.info("Realizando consulta a Alpaca en: {}", url);

        try {
            ResponseEntity<TradingDetailDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(crearHeaders()),
                    TradingDetailDTO.class
            );

            logger.info("Respuesta exitosa desde Alpaca para ID: {}", alpacaId);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("Error al consultar detalles desde Alpaca para ID: {} - Codigo de estado: {}, cuerpo: {}",
                    alpacaId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new SaldoException("Error al consultar detalles desde Alpaca para ID: " + alpacaId);
        }
    }

    private HttpHeaders crearHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, apiSecret);
        headers.setAccept(MediaType.parseMediaTypes("application/json"));
        return headers;
    }
}
