package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SaldoException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.TransferSyncException;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Transfer;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IOrderRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.ITransferRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.ISaldoService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaldoService implements ISaldoService {

    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final ITransferRepository transferRepository;
    private final IUserService userService;
    private final IOrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    @Value("${alpaca.broker.account-url}")
    private String transferUrl;

    public SaldoService(IAlpacaAccountRepository alpacaAccountRepository,
                        ITransferRepository transferRepository,
                        IUserService userService,
                        IOrderRepository orderRepository) {
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.transferRepository = transferRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    @Override
    public BigDecimal obtenerSaldoUsuarioActual() {
        AlpacaAccount cuenta = obtenerCuentaAlpacaDelUsuario();
        sincronizarTransferenciasPendientes(cuenta);

        BigDecimal totalTransferencias = obtenerTotalTransferencias(cuenta);
        BigDecimal totalCompras = obtenerTotalCompras(cuenta);
        BigDecimal totalVentas = obtenerTotalVentas(cuenta);

        return totalTransferencias.subtract(totalCompras).add(totalVentas);
    }

    private AlpacaAccount obtenerCuentaAlpacaDelUsuario() {
        User usuario = userService.getAuthenticatedUserEntity();
        return alpacaAccountRepository.findByUserId(usuario.getId())
                .orElseThrow(() -> new SaldoException("No se encontro cuenta Alpaca para el usuario."));
    }

    private BigDecimal obtenerTotalTransferencias(AlpacaAccount cuenta) {
        return transferRepository
                .sumByAlpacaAccountAndTypeAndStatus(cuenta, "ach", "COMPLETE")
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal obtenerTotalCompras(AlpacaAccount cuenta) {
        return orderRepository
                .sumComprasEjecutadasPorCuenta(cuenta)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal obtenerTotalVentas(AlpacaAccount cuenta) {
        return orderRepository
                .sumVentasEjecutadasPorCuenta(cuenta)
                .orElse(BigDecimal.ZERO);
    }

    private void sincronizarTransferenciasPendientes(AlpacaAccount cuenta) {
        List<Transfer> pendientes = transferRepository.findByAlpacaAccountAndStatusNot(cuenta, "COMPLETE");

        for (Transfer transferencia : pendientes) {
            try {
                String url = construirUrlTransferencias(cuenta.getAlpacaId());
                HttpEntity<String> entidad = construirHeaders();

                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entidad, String.class);
                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) continue;

                actualizarTransferenciaSiCambio(response.getBody(), transferencia);

            } catch (Exception e) {
                throw new TransferSyncException("Error sincronizando transferencia " + transferencia.getId(), e);
            }
        }
    }

    private String construirUrlTransferencias(String alpacaId) {
        return transferUrl + "/" + alpacaId + "/transfers";
    }

    private HttpEntity<String> construirHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, apiSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    private void actualizarTransferenciaSiCambio(String jsonBody, Transfer transferencia) {
        JSONArray lista = new JSONArray(jsonBody);

        for (int i = 0; i < lista.length(); i++) {
            JSONObject json = lista.getJSONObject(i);
            String transferIdApi = json.optString("id");

            if (!transferIdApi.equals(transferencia.getAlpacaTransferId())) continue;

            String nuevoEstado = json.optString("status");

            if (!transferencia.getStatus().equalsIgnoreCase(nuevoEstado)) {
                transferencia.setStatus(nuevoEstado);
                transferRepository.save(transferencia);
            }
            break;
        }
    }
}
