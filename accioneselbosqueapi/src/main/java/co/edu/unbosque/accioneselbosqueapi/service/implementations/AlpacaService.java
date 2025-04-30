package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.AlpacaAccountCreationException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.AlpacaDuplicateAccountException;
import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.UsuarioNoEncontradoException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AlpacaAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AlpacaAccountResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IAlpacaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;


@Service
public class AlpacaService implements IAlpacaService {

    @Value("${alpaca.broker.api.key}")
    private String apiKey;

    @Value("${alpaca.broker.api.secret}")
    private String apiSecret;

    @Value("${alpaca.broker.account-url}")
    private String accountUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final IAlpacaAccountRepository alpacaAccountRepository;
    private final IUserRepository userRepository;
    private final ModelMapper modelMapper;

    public AlpacaService(IAlpacaAccountRepository alpacaAccountRepository, IUserRepository userRepository, ModelMapper modelMapper) {
        this.alpacaAccountRepository = alpacaAccountRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AlpacaAccountResponseDTO crearCuenta(CreateAccountRequestDTO request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth(apiKey, apiSecret);

            AlpacaAccountRequestDTO alpacaRequest = new AlpacaAccountRequestDTO();
            alpacaRequest.setContact(request.getContact());
            alpacaRequest.setIdentity(request.getIdentity());
            alpacaRequest.setDisclosures(request.getDisclosures());
            alpacaRequest.setAgreements(request.getAgreements());
            alpacaRequest.setDocuments(request.getDocuments());
            alpacaRequest.setTrustedContact(request.getTrustedContact());
            alpacaRequest.setEnabledAssets(request.getEnabledAssets());

            HttpEntity<AlpacaAccountRequestDTO> entity = new HttpEntity<>(alpacaRequest, headers);

            ResponseEntity<AlpacaAccountResponseDTO> response = restTemplate.exchange(
                    accountUrl,
                    HttpMethod.POST,
                    entity,
                    AlpacaAccountResponseDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new AlpacaAccountCreationException("Error inesperado al crear la cuenta: " + response.getBody());
            }
        } catch (HttpClientErrorException.Conflict e) {
            throw new AlpacaDuplicateAccountException("Ya existe una cuenta en Alpaca con este correo.");
        } catch (HttpClientErrorException e) {
            throw new AlpacaAccountCreationException("Error al llamar a la API de Alpaca: " + e.getMessage());
        }
    }

    @Override
    public void guardarCuentaAlpaca(AlpacaAccountResponseDTO dto, Long idUsuario) {
        User usuario = userRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        AlpacaAccount cuenta = modelMapper.map(dto, AlpacaAccount.class);
        cuenta.setAlpacaId(dto.getId());
        cuenta.setUser(usuario);
        cuenta.setCreatedAt(LocalDateTime.now());

        alpacaAccountRepository.save(cuenta);
    }


}
