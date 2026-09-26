package com.momentum.habittracker.Exception;

/**
 * Exception thrown when a requested domain entity cannot be found.
 *
 * Purpose:
 * Signals a 404 Not Found condition when an entity (User, Habit, or HabitLog)
 * queried by ID, username, or composite key does not exist in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message description of the missing entity
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
