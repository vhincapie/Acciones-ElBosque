package co.edu.unbosque.accioneselbosqueapi.configurations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.WatchlistItemDTO;
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
    }

}
