package com.momentum.habittracker.Security;

import com.momentum.habittracker.Entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of Spring Security's {@link UserDetails}.
 *
 * Purpose:
 * Bridges the application's domain {@link User} entity with the Spring Security framework.
 * Encapsulates the user's identity, hashed credentials, and granted role authorities so
 * that Spring Security can perform authentication and authorization checks.
 * Also exposes domain-specific identifiers (ID and email) for convenient retrieval in controllers.
 */
public class CustomUserDetails implements UserDetails {

    /** The database primary key ID of the user */
    private final Long id;

    /** The unique username used as the principal identity */
    private final String username;

    /** The user's registered email address */
    private final String email;

    /** The hashed password stored in the database */
    private final String password;

    /** Granted authorities/roles mapped for Spring Security (e.g., ROLE_USER) */
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructs a CustomUserDetails instance from a domain {@link User} entity.
     *
     * @param user the domain User entity to wrap
     */
    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    /**
     * Returns the user's primary key ID.
     *
     * @return database ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the user's email address.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the authorities granted to the user.
     *
     * @return list containing the user's role authority
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the hashed password used to authenticate the user.
     *
     * @return password hash
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * Returns the username used to authenticate the user.
     *
     * @return username
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return always true (account expiration not enforced)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return always true (account locking not enforced)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the user's credentials (password) has expired.
     *
     * @return always true (credential expiration not enforced)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return always true (active by default)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
