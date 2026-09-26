package com.momentum.habittracker.DTO;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for user authentication/login requests.
 *
 * Purpose:
 * Encapsulates the credentials sent by a user attempting to log in. Supports both username
 * and email in a single input field to provide a flexible user experience.
 *
 * @param usernameOrEmail The user's username or registered email address (required)
 * @param password The raw password to authenticate (required)
 */
public record UserLoginDTO(
        @NotBlank(message = "Username or email is required")
        String usernameOrEmail,

        @NotBlank(message = "Password is required")
        String password
) {}
