package co.edu.unbosque.accioneselbosqueapi.configurations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import co.edu.unbosque.accioneselbosqueapi.model.entity.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE);
        cargarMapeos(modelMapper);
        return modelMapper;
    }

    private void cargarMapeos(ModelMapper modelMapper) {

        // Watchlist
        modelMapper.typeMap(WatchlistItem.class, WatchlistItemDTO.class)
                .addMappings(mapper -> mapper.map(WatchlistItem::getId, WatchlistItemDTO::setId));
        modelMapper.typeMap(WatchlistItemDTO.class, WatchlistItem.class)
                .addMappings(mapper -> mapper.map(WatchlistItemDTO::getId, WatchlistItem::setId));

        // CreateAccountRequestDTO -> AlpacaAccountRequestDTO
        modelMapper.createTypeMap(CreateAccountRequestDTO.class, AlpacaAccountRequestDTO.class)
                .addMappings(mapper -> mapper.skip(AlpacaAccountRequestDTO::setEnabledAssets));

        // AlpacaAccountResponseDTO -> AlpacaAccount
        modelMapper.typeMap(AlpacaAccountResponseDTO.class, AlpacaAccount.class)
                .addMappings(mapper -> {
                    mapper.map(AlpacaAccountResponseDTO::getId, AlpacaAccount::setAlpacaId); // UUID de Alpaca
                    mapper.skip(AlpacaAccount::setId);
                });

        // AlpacaAccount -> AlpacaAccountUserResponseDTO
        modelMapper.typeMap(AlpacaAccount.class, AlpacaAccountUserResponseDTO.class);

        // UserDTO -> User
        modelMapper.createTypeMap(UserDTO.class, User.class)
                .addMappings(mapper -> {
                    mapper.map(UserDTO::getRole, User::setRole);
                    mapper.map(UserDTO::getSubrole, User::setSubrole);
                    mapper.skip(User::setSubscription);
                    mapper.skip(User::setMercadoInteres);
                })
                .setPostConverter(context -> {
                    UserDTO source = context.getSource();
                    User destination = context.getDestination();

                    if (source.getMarketInterestDTO() != null && source.getMarketInterestDTO().getId() != null) {
                        MarketInterest mercado = new MarketInterest();
                        mercado.setId(source.getMarketInterestDTO().getId());
                        destination.setMercadoInteres(mercado);
                    }

                    return destination;
                });

        // User -> UserDTO
        modelMapper.createTypeMap(User.class, UserDTO.class)
                .addMappings(mapper -> {
                    mapper.map(User::getRole, UserDTO::setRole);
                    mapper.map(User::getSubrole, UserDTO::setSubrole);
                    mapper.skip(UserDTO::setSubscription);
                    mapper.skip(UserDTO::setMarketInterestDTO);
                })
                .setPostConverter(context -> {
                    User source = context.getSource();
                    UserDTO destination = context.getDestination();

                    if (source.getMercadoInteres() != null) {
                        MarketInterestDTO dto = new MarketInterestDTO();
                        dto.setId(source.getMercadoInteres().getId());
                        dto.setNombre(source.getMercadoInteres().getNombre());
                        destination.setMarketInterestDTO(dto);
                    }

                    return destination;
                });

        // UserSubscription <-> UserSubscriptionDTO
        modelMapper.createTypeMap(UserSubscription.class, UserSubscriptionDTO.class)
                .addMappings(mapper -> mapper.skip(UserSubscriptionDTO::setUser));

        // UserSubscriptionDTO <-> UserSubscription
        modelMapper.createTypeMap(UserSubscriptionDTO.class, UserSubscription.class)
                .addMappings(mapper -> mapper.skip(UserSubscription::setUser));

    }
}
