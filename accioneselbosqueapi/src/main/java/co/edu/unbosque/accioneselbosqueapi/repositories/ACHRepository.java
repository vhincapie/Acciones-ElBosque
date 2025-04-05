package co.edu.unbosque.accioneselbosqueapi.repositories;

import co.edu.unbosque.accioneselbosqueapi.model.entity.ACHRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ACHRepository extends JpaRepository<ACHRelationShip, Integer> {
}
