package co.edu.unbosque.accioneselbosqueapi.repository;

import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.model.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface IWatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    boolean existsByUserAndSymbol(User user, String symbol);
}

