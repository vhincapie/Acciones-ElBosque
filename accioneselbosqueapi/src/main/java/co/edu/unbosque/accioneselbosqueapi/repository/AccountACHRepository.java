package co.edu.unbosque.accioneselbosqueapi.repository;

import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountACHRepository extends JpaRepository<AccountACHRelationShip, Integer> {
}
