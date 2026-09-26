package com.momentum.habittracker.DTO;

import com.momentum.habittracker.Entities.HabitLog;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Data Transfer Object for creating or updating a habit log entry.
 *
 * Purpose:
 * Encapsulates check-in information when a user records their progress on a habit
 * for a specific day (completion status and optional journal/reflection notes).
 *
 * @param date The calendar date the habit log applies to, required
 * @param status The completion status (DONE, MISSED, RECOVERED), required
 * @param notes Optional personal reflection or notes for this log
 */
public record HabitLogRequestDTO(
        @NotNull(message = "Date is required")
        LocalDate date,

        @NotNull(message = "Status is required")
        HabitLog.Status status,

        String notes
) {}
