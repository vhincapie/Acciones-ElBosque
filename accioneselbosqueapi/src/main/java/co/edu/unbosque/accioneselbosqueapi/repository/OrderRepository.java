package co.edu.unbosque.accioneselbosqueapi.repository;

import co.edu.unbosque.accioneselbosqueapi.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
