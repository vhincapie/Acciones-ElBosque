package co.edu.unbosque.accioneselbosqueapi.configurations;

import co.edu.unbosque.accioneselbosqueapi.model.DTO.ResponseACHDTO;
import co.edu.unbosque.accioneselbosqueapi.model.entity.ACHRelationShip;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        return mapper;
    }
}
