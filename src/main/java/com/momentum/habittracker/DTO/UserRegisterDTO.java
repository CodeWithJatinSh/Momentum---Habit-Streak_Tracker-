package com.momentum.habittracker.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user registration requests.
 * Encapsulates and validates initial registration credentials.
 *
 * @param username Unique username, must be between 3 and 30 characters
 * @param email Valid email address used for identification
 * @param password Raw password, minimum 8 characters
 */
public record UserRegisterDTO(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be 3-30 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}