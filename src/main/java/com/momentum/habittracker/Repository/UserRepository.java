package com.momentum.habittracker.Repository;

import com.momentum.habittracker.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link User} entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the User if found, or empty
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, or empty
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user already exists with the given email.
     *
     * @param email the email to check
     * @return true if an account with this email exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user already exists with the given username.
     *
     * @param username the username to check
     * @return true if an account with this username exists, false otherwise
     */
    boolean existsByUsername(String username);
}
