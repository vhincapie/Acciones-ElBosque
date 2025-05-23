package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.ACHBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.AccountACHRelationShipDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.repository.IAccountACHRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IAlpacaAccountRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountACHServiceTest {

    private AccountACHService service;
    private IAccountACHRepository achRepository;
    private IAlpacaAccountRepository alpacaAccountRepository;
    private IUserService userService;
    private ModelMapper modelMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        achRepository = mock(IAccountACHRepository.class);
        alpacaAccountRepository = mock(IAlpacaAccountRepository.class);
        userService = mock(IUserService.class);
        modelMapper = new ModelMapper();
        objectMapper = new ObjectMapper();
        OkHttpClient httpClient = mock(OkHttpClient.class);

        service = new AccountACHService(httpClient, achRepository, alpacaAccountRepository, userService, modelMapper, objectMapper);
    }

    @Test
    void getAllForAuthenticatedUserReturnsEmptyList() {
        // Arrange
        User user = new User();
        user.setId(1L);

        AlpacaAccount alpacaAccount = new AlpacaAccount();
        alpacaAccount.setId(1L);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(user.getId())).thenReturn(Optional.of(alpacaAccount));
        when(achRepository.findByAlpacaAccount(alpacaAccount)).thenReturn(Collections.emptyList());

        // Act
        List<AccountACHRelationShipDTO> result = service.getAllForAuthenticatedUser();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllForAuthenticatedUserWithoutAlpacaAccountThrowsException() {
        // Arrange
        User user = new User();
        user.setId(99L);

        when(userService.getAuthenticatedUserEntity()).thenReturn(user);
        when(alpacaAccountRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ACHBadRequestException.class, () -> service.getAllForAuthenticatedUser());
    }
}
