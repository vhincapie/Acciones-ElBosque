package co.edu.unbosque.accioneselbosqueapi.configurations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.*;
import co.edu.unbosque.accioneselbosqueapi.model.entity.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        cargarMapeos(modelMapper);
        return modelMapper;
    }

    private void cargarMapeos(ModelMapper modelMapper) {
        modelMapper.typeMap(WatchlistItem.class, WatchlistItemDTO.class).addMappings
                (mapper -> mapper.map(WatchlistItem::getId, WatchlistItemDTO::setId));


        modelMapper.typeMap(WatchlistItemDTO.class, WatchlistItem.class).addMappings(mapper -> {
            mapper.map(WatchlistItemDTO::getId, WatchlistItem::setId);
        });

        modelMapper.typeMap(UserDTO.class, User.class)
                .addMappings(mapper -> {
                    mapper.map(UserDTO::getRole, User::setRole);
                    mapper.map(UserDTO::getSubrole, User::setSubrole);
                });

        modelMapper.typeMap(User.class, UserDTO.class)
                .addMappings(mapper -> {
                    mapper.map(User::getRole, UserDTO::setRole);
                    mapper.map(User::getSubrole, UserDTO::setSubrole);
                });

        modelMapper.createTypeMap(CreateAccountRequestDTO.class, AlpacaAccountRequestDTO.class)
                .addMappings(mapper -> mapper.skip(AlpacaAccountRequestDTO::setEnabledAssets));

        // Mapeo para AccountACHRelationShip
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

        // Mapeo para Transfer
        TypeMap<ResponseTransferDTO, Transfer> transferMap = modelMapper.createTypeMap(ResponseTransferDTO.class, Transfer.class);
        transferMap.addMappings(m -> {
            m.map(ResponseTransferDTO::getType, Transfer::setType);
            m.map(ResponseTransferDTO::getAmount, Transfer::setAmount);
            m.map(ResponseTransferDTO::getCurrency, Transfer::setCurrency);
            m.skip(Transfer::setId);
        });

        // Mapeo para Order
        TypeMap<OrderResponseDTO, Order> orderMap = modelMapper.createTypeMap(OrderResponseDTO.class, Order.class);
        orderMap.addMappings(m -> {
            m.skip(Order::setId);
            m.skip(Order::setTransactionAmount);
            m.skip(Order::setCommissionAmount);
            m.skip(Order::setNetAmount);
        });
    }
}
