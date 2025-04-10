package co.edu.unbosque.accioneselbosqueapi.configurations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.AlpacaAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.CreateAccountRequestDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.UserDTO;
import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.model.entity.WatchlistItem;
import org.modelmapper.ModelMapper;
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


    }

}
