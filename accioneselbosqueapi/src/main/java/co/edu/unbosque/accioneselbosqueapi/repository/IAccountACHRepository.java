package co.edu.unbosque.accioneselbosqueapi.repository;

import co.edu.unbosque.accioneselbosqueapi.model.entity.AccountACHRelationShip;
import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAccountACHRepository extends JpaRepository<AccountACHRelationShip, Long> {

    Optional<AccountACHRelationShip> findFirstByAlpacaAccount(AlpacaAccount alpacaAccount);

}
