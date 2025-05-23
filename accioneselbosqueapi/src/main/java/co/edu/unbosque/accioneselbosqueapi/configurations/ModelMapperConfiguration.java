package co.edu.unbosque.accioneselbosqueapi.configurations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import co.edu.unbosque.accioneselbosqueapi.model.entity.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(AccessLevel.PRIVATE)
                .setAmbiguityIgnored(true);
        cargarMapeos(modelMapper);
        return modelMapper;
    }

    private void cargarMapeos(ModelMapper modelMapper) {

        // WatchlistItem → WatchlistItemDTO
        modelMapper.createTypeMap(WatchlistItemDTO.class, WatchlistItem.class)
                .addMappings(mapper -> {
                    mapper.map(WatchlistItemDTO::getId, WatchlistItem::setId);
                    mapper.skip(WatchlistItem::setUser); // ⚠️ Evita el error de conversión
                });

        // WatchlistItem → WatchlistItemDTO
        modelMapper.createTypeMap(WatchlistItem.class, WatchlistItemDTO.class)
                .addMappings(mapper -> {
                    mapper.map(WatchlistItem::getId, WatchlistItemDTO::setId);
                    mapper.map(src -> src.getUser().getId(), WatchlistItemDTO::setUser); // Para mostrar el id del usuario si se requiere
                });


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

        // UserSubscription -> UserSubscriptionDTO
        modelMapper.createTypeMap(UserSubscription.class, UserSubscriptionDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getUser().getId(), UserSubscriptionDTO::setUser));

        // UserSubscriptionDTO -> UserSubscription
        modelMapper.createTypeMap(UserSubscriptionDTO.class, UserSubscription.class)
                .addMappings(mapper -> mapper.skip(UserSubscription::setUser));


        // AccountACHRelationShipDTO → AccountACHRelationShip
        TypeMap<ResponseAccountACHDTO, AccountACHRelationShip> accountACHMap =
                modelMapper.createTypeMap(ResponseAccountACHDTO.class, AccountACHRelationShip.class);

        accountACHMap.addMappings(m -> {
            m.map(ResponseAccountACHDTO::getId, AccountACHRelationShip::setAchId);
            m.map(ResponseAccountACHDTO::getAccountOwnerName, AccountACHRelationShip::setAccountOwnerName);
            m.map(ResponseAccountACHDTO::getBankAccountType, AccountACHRelationShip::setBankAccountType);
            m.map(ResponseAccountACHDTO::getBankAccountNumber, AccountACHRelationShip::setBankAccountNumber);
            m.map(ResponseAccountACHDTO::getNickname, AccountACHRelationShip::setNickname);

            m.skip(AccountACHRelationShip::setId);
        });

        // AccountACHRelationShip → AccountACHRelationShipDTO
        modelMapper.createTypeMap(AccountACHRelationShip.class, AccountACHRelationShipDTO.class)
                .addMappings(mapper -> mapper.map(src -> src.getAlpacaAccount().getId(), AccountACHRelationShipDTO::setAlpacaAccountId));


        // TransferDTO → Transfer
        modelMapper.createTypeMap(ResponseTransferDTO.class, Transfer.class)
                .addMappings(mapper -> {
                    mapper.skip(Transfer::setId); // 🔒 evita conflicto String → Long
                    mapper.map(ResponseTransferDTO::getId, Transfer::setAlpacaTransferId);
                    mapper.map(ResponseTransferDTO::getType, Transfer::setType);
                    mapper.map(ResponseTransferDTO::getAmount, Transfer::setAmount);
                    mapper.map(ResponseTransferDTO::getCurrency, Transfer::setCurrency);
                    mapper.map(ResponseTransferDTO::getStatus, Transfer::setStatus);
                });

        // OrderDTO → Order
        modelMapper.createTypeMap(OrderResponseDTO.class, Order.class)
                .addMappings(m -> {
                    m.skip(Order::setId);
                    m.map(OrderResponseDTO::getId, Order::setAlpacaOrderId);
                    m.map(OrderResponseDTO::getSymbol, Order::setSymbol);
                    m.map(OrderResponseDTO::getOrderType, Order::setOrderType);
                    m.map(OrderResponseDTO::getSide, Order::setSide);
                    m.map(OrderResponseDTO::getTimeInForce, Order::setTimeInForce);
                    m.map(OrderResponseDTO::getAssetClass, Order::setAssetClass);
                    m.map(OrderResponseDTO::getStatus, Order::setStatus);

                    m.map(src -> src.getQty() != null ? new BigDecimal(src.getQty()) : null, Order::setQty);
                    m.map(src -> src.getLimitPrice() != null ? new BigDecimal(src.getLimitPrice()) : null, Order::setLimitPrice);
                    m.map(src -> src.getStopPrice() != null ? new BigDecimal(src.getStopPrice()) : null, Order::setStopPrice);

                    m.skip(Order::setTransactionAmount);
                    m.skip(Order::setCommissionAmount);
                    m.skip(Order::setNetAmount);
                });

        // AccountTradingDetailDTO → AlpacaAccount
        modelMapper.createTypeMap(TradingDetailDTO.class, AlpacaAccount.class)
                .addMappings(mapper -> {
                    mapper.map(
                            src -> src.getBuyingPower() != null ? new BigDecimal(src.getBuyingPower()) : null,
                            AlpacaAccount::setBuyingPower
                    );
                });
    }
}
