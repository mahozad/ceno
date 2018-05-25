package ir.ceno.repository;

import ir.ceno.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by his/her username.
     *
     * @param name the name of the user to find by
     * @return {@link Optional} containing the user or empty if not found
     */
    Optional<User> findByName(String name);

    /**
     * Checks whether the given username already exists or not.
     *
     * @param name the username to check for
     * @return true if username exists, false otherwise
     */
    boolean existsByName(String name);
}
