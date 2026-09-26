package com.momentum.habittracker.DTO;

/**
 * Data Transfer Object returned upon successful authentication or registration.
 *
 * Purpose:
 * Transmits the issued cryptographic JWT access token and basic identity metadata
 * back to the client application so it can persist the token and identify the user.
 *
 * @param token The generated JSON Web Token (JWT) string
 * @param type The token type header prefix (typically "Bearer")
 * @param userId The primary key ID of the authenticated user
 * @param username The username of the authenticated user
 * @param email The email address of the authenticated user
 */
public record AuthResponseDTO(
        String token,
        String type,
        Long userId,
        String username,
        String email
) {
    /**
     * Convenience constructor defaulting token type to "Bearer".
     *
     * @param token The JWT access token
     * @param userId The authenticated user's ID
     * @param username The authenticated user's username
     * @param email The authenticated user's email
     */
    public AuthResponseDTO(String token, Long userId, String username, String email) {
        this(token, "Bearer", userId, username, email);
    }
}
