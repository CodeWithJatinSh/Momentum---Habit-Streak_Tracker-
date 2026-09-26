package com.momentum.habittracker.DTO;

import com.momentum.habittracker.Entities.User;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing safe public user details.
 *
 * Purpose:
 * Projects the {@link User} entity for client responses (such as GET /api/auth/me) while
 * strictly omitting sensitive credentials like password hashes to prevent data leakage.
 *
 * @param id The user's primary key identifier
 * @param username The user's unique username
 * @param email The user's email address
 * @param role The authorization role assigned to the user (USER or ADMIN)
 * @param createdAt The timestamp when the account was registered
 */
public record UserResponseDTO(
        Long id,
        String username,
        String email,
        User.Role role,
        LocalDateTime createdAt
) {}
