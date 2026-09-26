package com.momentum.habittracker.DTO;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response payload returned by API endpoints upon failure.
 *
 * Purpose:
 * Provides a predictable, unified error format for frontend clients. Includes the HTTP status,
 * machine-readable error type, human-friendly message, request path, timestamp, and an optional
 * map of field-level validation errors.
 *
 * @param status HTTP status code integer (e.g., 400, 401, 404, 409, 500)
 * @param error HTTP status reason phrase (e.g., "Bad Request", "Unauthorized")
 * @param message Human-readable error description
 * @param path Request URI that triggered the error
 * @param validationErrors Optional map of field-level constraint violations (fieldName -> errorMessage)
 * @param timestamp Time when the error occurred
 */
public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors,
        LocalDateTime timestamp
) {
    /**
     * Convenience constructor for general errors without field-level validation details.
     *
     * @param status HTTP status code integer
     * @param error HTTP status reason phrase
     * @param message Human-readable error description
     * @param path Request URI that triggered the error
     */
    public ErrorResponseDTO(int status, String error, String message, String path) {
        this(status, error, message, path, null, LocalDateTime.now());
    }

    /**
     * Convenience constructor for validation failure errors including field violations.
     *
     * @param status HTTP status code integer
     * @param error HTTP status reason phrase
     * @param message Human-readable error description
     * @param path Request URI that triggered the error
     * @param validationErrors Map of field names to error messages
     */
    public ErrorResponseDTO(int status, String error, String message, String path, Map<String, String> validationErrors) {
        this(status, error, message, path, validationErrors, LocalDateTime.now());
    }
}
