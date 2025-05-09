package co.edu.unbosque.accioneselbosqueapi.repository;

import co.edu.unbosque.accioneselbosqueapi.model.entity.AlpacaAccount;
import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {


    @Query("SELECT SUM(o.netAmount) FROM Order o WHERE o.alpacaAccount = :cuenta AND o.side = 'buy' AND o.status = 'filled'")
    Optional<BigDecimal> sumComprasEjecutadasPorCuenta(AlpacaAccount cuenta);

    @Query("SELECT SUM(o.netAmount) FROM Order o WHERE o.alpacaAccount = :cuenta AND o.side = 'sell' AND o.status = 'filled'")
    Optional<BigDecimal> sumVentasEjecutadasPorCuenta(AlpacaAccount cuenta);

    List<Order> findByAlpacaAccountAndStatus(AlpacaAccount account, String status);


}
