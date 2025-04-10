package co.edu.unbosque.accioneselbosqueapi.service.interfaces;

import co.edu.unbosque.accioneselbosqueapi.model.entity.MarketInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IMarketInterestRepository extends JpaRepository<MarketInterest, Long> {
    Optional<MarketInterest> findByNombre(String nombre);
}
