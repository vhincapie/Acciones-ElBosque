package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import java.util.List;
import java.util.Optional;

public interface IService <T,K>{

    void create(T dto);
    Optional<T> find(K id);
    void update(K id, T dto);
    void delete(K id);
    List<T> findAll();

}