package es.udc.paproject.backend.model.entities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

public interface UserDao extends JpaRepository<User, Long>  {

    boolean existsByUserName(String userName);

    Optional<User> findByUserName(String userName);

    Optional<User> findByEmail(String email);

    List<User> findByFirstName(String firstName);

    List<User> findByLastName(String lastName);

    List<User> findByFirstNameAndLastName(String firstName, String lastName);

    List<User> findByRole(RoleType role);

}
