package com.momentum.habittracker.Security;

import com.momentum.habittracker.Entities.User;
import com.momentum.habittracker.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to load user-specific data during authentication.
 *
 * Purpose:
 * Implements Spring Security's {@link UserDetailsService} contract.
 * Queries the database via {@link UserRepository} to locate users by either their username
 * or email address, converting the found domain {@link User} into a {@link CustomUserDetails}
 * object suitable for authentication and token validation.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /** Repository used to query user records from the database */
    private final UserRepository userRepository;

    /**
     * Constructs the service with required UserRepository dependency.
     *
     * @param userRepository database repository for User entities
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Locates the user based on the username or email.
     *
     * @param usernameOrEmail the identifier identifying the user whose data is required
     * @return a fully populated UserDetails object for authentication
     * @throws UsernameNotFoundException if the user could not be found by username or email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username or email: " + usernameOrEmail
                ));

        return new CustomUserDetails(user);
    }
}
