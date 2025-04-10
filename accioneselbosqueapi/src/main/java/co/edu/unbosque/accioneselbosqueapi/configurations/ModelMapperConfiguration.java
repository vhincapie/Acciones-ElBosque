package co.edu.unbosque.accioneselbosqueapi.configurations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseACHDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseAccountACHDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.ACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.model.entity.WatchlistItem;
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

        // Mapeo personalizado de ResponseAccountACHDTO → AccountACHRelationShip
        TypeMap<ResponseAccountACHDTO, AccountACHRelationShip> accountACHMap =
                modelMapper.createTypeMap(ResponseAccountACHDTO.class, AccountACHRelationShip.class);

        accountACHMap.addMappings(m -> {
            m.map(ResponseAccountACHDTO::getId, AccountACHRelationShip::setAchId);
            m.map(ResponseAccountACHDTO::getAccountId, AccountACHRelationShip::setAccountId);
            m.map(ResponseAccountACHDTO::getAccountOwnerName, AccountACHRelationShip::setAccountOwnerName);
            m.map(ResponseAccountACHDTO::getBankAccountType, AccountACHRelationShip::setBankAccountType);
            m.map(ResponseAccountACHDTO::getBankAccountNumber, AccountACHRelationShip::setBankAccountNumber);
            m.map(ResponseAccountACHDTO::getBankRoutingNumber, AccountACHRelationShip::setBankRoutingNumber);
            m.map(ResponseAccountACHDTO::getNickname, AccountACHRelationShip::setNickname);

            m.skip(AccountACHRelationShip::setId); // ID generado por la base de datos
        });

        TypeMap<ResponseACHDTO, ACHRelationShip> achMap = modelMapper.createTypeMap(ResponseACHDTO.class, ACHRelationShip.class);
        achMap.addMappings(m -> {
            m.map(ResponseACHDTO::getId, ACHRelationShip::setAchId);
            m.map(ResponseACHDTO::getAccountId, ACHRelationShip::setAccountId);
            m.map(ResponseACHDTO::getName, ACHRelationShip::setName);
            m.map(ResponseACHDTO::getBankCode, ACHRelationShip::setBankCode);
            m.map(ResponseACHDTO::getBankCodeType, ACHRelationShip::setBankCodeType);
            m.map(ResponseACHDTO::getCountry, ACHRelationShip::setCountry);
            m.map(ResponseACHDTO::getStateProvince, ACHRelationShip::setStateProvince);
            m.map(ResponseACHDTO::getPostalCode, ACHRelationShip::setPostalCode);
            m.map(ResponseACHDTO::getCity, ACHRelationShip::setCity);
            m.map(ResponseACHDTO::getStreetAddress, ACHRelationShip::setStreetAddress);
            m.map(ResponseACHDTO::getAccountNumber, ACHRelationShip::setAccountNumber);
            m.skip(ACHRelationShip::setId); // El ID es autogenerado en la BD
        });

    }

}
