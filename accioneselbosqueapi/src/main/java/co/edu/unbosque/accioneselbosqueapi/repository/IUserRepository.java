package co.edu.unbosque.accioneselbosqueapi.repository;


import co.edu.unbosque.accioneselbosqueapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByResetToken(String resetToken);


}
