package co.edu.unbosque.accioneselbosqueapi.repository;

import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IAlpacaAccountRepository extends JpaRepository<AlpacaAccount, Long> {

    Optional<AlpacaAccount> findByUserId(Long userId);
}
