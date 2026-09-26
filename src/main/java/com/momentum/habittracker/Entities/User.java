package com.momentum.habittracker.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing an application user.
 * Manages user credentials, role-based authorization, and profile metadata.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Primary key identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    /**
     * Unique username used for identification and display.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Unique email address used for login and notifications.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Encrypted/hashed password for authentication.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Authorization role of the user (e.g., USER, ADMIN).
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    /**
     * Timestamp when the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * Lifecycle hook executed automatically before persisting a new user.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Roles available for application users.
     */
    public enum Role {
        USER, ADMIN
    }
}