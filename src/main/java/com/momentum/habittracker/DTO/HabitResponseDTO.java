package com.momentum.habittracker.DTO;

import com.momentum.habittracker.Entities.Habit;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object representing habit details returned to clients.
 *
 * Purpose:
 * Represents the habit state displayed in dashboards and list views, combining basic
 * habit information with live computed metrics like current streak, longest streak, and
 * active status without exposing the internal User entity structure.
 *
 * @param id The habit's primary key identifier
 * @param title The title of the habit
 * @param description Detailed description of the habit
 * @param frequency Frequency cadence (DAILY, WEEKLY, MONTHLY)
 * @param currentStreak The current unbroken streak count
 * @param longestStreak The all-time highest streak count
 * @param lastCompletedDate The date when this habit was last marked done
 * @param active Whether the habit is actively tracked or archived
 * @param createdAt The timestamp when the habit was created
 */
public record HabitResponseDTO(
        Long id,
        String title,
        String description,
        Habit.Frequency frequency,
        int currentStreak,
        int longestStreak,
        LocalDate lastCompletedDate,
        boolean active,
        LocalDateTime createdAt
) {}
