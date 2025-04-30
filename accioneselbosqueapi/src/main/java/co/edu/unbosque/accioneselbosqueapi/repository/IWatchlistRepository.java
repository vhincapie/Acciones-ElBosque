package co.edu.unbosque.accioneselbosqueapi.repository;

import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import co.edu.unbosque.accioneselbosqueapi.model.entity.WatchlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface IWatchlistRepository extends JpaRepository<WatchlistItem, Long> {

    List<WatchlistItem> findAllByUser(User user);
    boolean existsByUserAndSymbol(User user, String symbol);
}

