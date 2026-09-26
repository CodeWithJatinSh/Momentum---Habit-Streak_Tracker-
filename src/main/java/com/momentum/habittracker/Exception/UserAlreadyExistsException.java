package com.momentum.habittracker.Exception;

/**
 * Exception thrown when attempting to register a user with an existing username or email.
 *
 * Purpose:
 * Signals a 409 Conflict situation during user sign-up when unique constraints
 * on username or email are violated.
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     *
     * @param message description of which identifier is already taken
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
