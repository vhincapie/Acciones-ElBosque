package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.AlpacaAccountResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AlpacaAccountUserResponseDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.CreateAccountRequestDTO;

public interface IAlpacaService {
    AlpacaAccountResponseDTO crearCuenta(CreateAccountRequestDTO request);
    void guardarCuentaAlpaca(AlpacaAccountResponseDTO dto, Long idUsuario);
    AlpacaAccountUserResponseDTO obtenerCuentaPorUsuario(String username);
}
