package com.momentum.habittracker.DTO;

import com.momentum.habittracker.Entities.Habit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for creating or updating a habit.
 *
 * Purpose:
 * Encapsulates and validates input payload sent by users when defining a new habit
 * or modifying an existing one (title, optional description, and frequency cadence).
 *
 * @param title The title/name of the habit, required, max 100 characters
 * @param description Optional details, instructions, or motivation, max 500 characters
 * @param frequency Required repetition cadence (DAILY, WEEKLY, MONTHLY)
 */
public record HabitRequestDTO(
        @NotBlank(message = "Habit title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Frequency is required")
        Habit.Frequency frequency
) {}
