package com.momentum.habittracker.DTO;

import com.momentum.habittracker.Entities.HabitLog;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing habit log entries returned to clients.
 *
 * Purpose:
 * Returns the confirmation of a logged habit check-in, or items in a history/calendar
 * query, decoupling database associations from the JSON presentation layer.
 *
 * @param id The log entry's primary key identifier
 * @param habitId The ID of the parent habit
 * @param date The date the log applies to
 * @param status The logged completion status (DONE, MISSED, RECOVERED)
 * @param notes Optional notes recorded with the completion
 * @param loggedAt The exact timestamp when this entry was created
 */
public record HabitLogResponseDTO(
        Long id,
        Long habitId,
        LocalDate date,
        HabitLog.Status status,
        String notes,
        LocalDateTime loggedAt
) {}
