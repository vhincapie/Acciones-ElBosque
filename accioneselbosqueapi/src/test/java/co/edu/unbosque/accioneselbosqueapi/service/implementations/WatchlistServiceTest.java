package co.edu.unbosque.accioneselbosqueapi.service.implementations;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.WatchlistBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.StockDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.model.entity.UserSubscription;
import co.edu.unbosque.accioneselbosqueapi.model.entity.WatchlistItem;
import co.edu.unbosque.accioneselbosqueapi.repository.IUserSubscriptionRepository;
import co.edu.unbosque.accioneselbosqueapi.repository.IWatchlistRepository;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IMarketService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IUserService;
import co.edu.unbosque.accioneselbosqueapi.service.interfaces.IWatchlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WatchlistServiceTest {

    private IWatchlistRepository watchlistRepository;
    private IUserSubscriptionRepository subscriptionRepository;
    private IMarketService marketService;
    private IUserService userService;
    private ModelMapper modelMapper;
    private IWatchlistService service;

    @BeforeEach
    void setUp() {
        watchlistRepository = mock(IWatchlistRepository.class);
        subscriptionRepository = mock(IUserSubscriptionRepository.class);
        marketService = mock(IMarketService.class);
        userService = mock(IUserService.class);
        modelMapper = new ModelMapper();

        modelMapper.typeMap(WatchlistItem.class, WatchlistItemDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), WatchlistItemDTO::setUser));

        service = spy(new WatchlistService(watchlistRepository, modelMapper, marketService, userService, subscriptionRepository));
        doNothing().when(service).validarSimbolo(anyString());
        doReturn(new StockDTO()).when(service).findBySymbol(anyString());
    }

    @Test
    void createDebeCrearItemWatchlistCorrectamente() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userService.getAuthenticatedUserEntity()).thenReturn(user);

        UserSubscription subscription = new UserSubscription();
        subscription.setActivo(true);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(subscription));

        WatchlistItemDTO dto = new WatchlistItemDTO();
        dto.setSymbol("AAPL");
        dto.setCompanyName("Apple Inc");

        when(watchlistRepository.existsByUserAndSymbol(user, "AAPL")).thenReturn(false);

        WatchlistItem saved = new WatchlistItem();
        saved.setId(1L);
        saved.setSymbol("AAPL");
        saved.setCompanyName("Apple Inc");
        saved.setUser(user);
        saved.setCreatedAt(LocalDateTime.now());

        when(watchlistRepository.save(any(WatchlistItem.class))).thenReturn(saved);

        // Act
        WatchlistItemDTO resultado = service.create(dto);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getSymbol()).isEqualTo("AAPL");
    }

    @Test
    void deleteDebeEliminarItemSiEsDelUsuario() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userService.getAuthenticatedUserEntity()).thenReturn(user);

        UserSubscription subscription = new UserSubscription();
        subscription.setActivo(true);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(subscription));

        WatchlistItem item = new WatchlistItem();
        item.setId(99L);
        item.setUser(user);

        when(watchlistRepository.findById(99L)).thenReturn(Optional.of(item));

        // Act & Assert
        assertDoesNotThrow(() -> service.delete(99L));
        verify(watchlistRepository).deleteById(99L);
    }

    @Test
    void updateAlertPricesDebeActualizarPreciosCorrectamente() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userService.getAuthenticatedUserEntity()).thenReturn(user);

        UserSubscription subscription = new UserSubscription();
        subscription.setActivo(true);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(subscription));

        WatchlistItem item = new WatchlistItem();
        item.setId(88L);
        item.setUser(user);

        when(watchlistRepository.findById(88L)).thenReturn(Optional.of(item));

        // Act
        service.updateAlertPrices(88L, 100.0, 200.0);

        // Assert
        assertThat(item.getAlertMinPrice()).isEqualTo(100.0);
        assertThat(item.getAlertMaxPrice()).isEqualTo(200.0);
        verify(watchlistRepository).save(item);
    }

    @Test
    void updateAlertPricesDebeLanzarExcepcionSiMinMayorQueMax() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userService.getAuthenticatedUserEntity()).thenReturn(user);

        UserSubscription subscription = new UserSubscription();
        subscription.setActivo(true);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(subscription));

        WatchlistItem item = new WatchlistItem();
        item.setId(88L);
        item.setUser(user);

        when(watchlistRepository.findById(88L)).thenReturn(Optional.of(item));

        // Act & Assert
        assertThrows(WatchlistBadRequestException.class, () -> service.updateAlertPrices(88L, 300.0, 200.0));
    }

    @Test
    void findAllDebeRetornarSoloItemsDelUsuarioAutenticado() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userService.getAuthenticatedUserEntity()).thenReturn(user);

        UserSubscription subscription = new UserSubscription();
        subscription.setActivo(true);
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(subscription));

        WatchlistItem item = new WatchlistItem();
        item.setId(1L);
        item.setSymbol("TSLA");
        item.setUser(user);

        when(watchlistRepository.findAll()).thenReturn(List.of(item));

        StockDTO stockDTO = new StockDTO();
        stockDTO.setSymbol("TSLA");
        stockDTO.setCurrentPrice(650.0);
        stockDTO.setHighPrice(670.0);
        stockDTO.setLowPrice(640.0);
        stockDTO.setPreviousClosePrice(645.0);

        doReturn(stockDTO).when(service).findBySymbol("TSLA");

        // Act
        List<WatchlistItemDTO> resultado = service.findAll();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getSymbol()).isEqualTo("TSLA");
        assertThat(resultado.get(0).getCurrentPrice()).isEqualTo(650.0);
    }
}