package com.momentum.habittracker.DTO;

import com.momentum.habittracker.Entities.Habit;

/**
 * Data Transfer Object representing a habit's status for the current day.
 *
 * Purpose:
 * Powers the interactive daily habit list on client dashboards, indicating whether each
 * habit has already been completed today or remains pending.
 *
 * @param habitId The primary key identifier of the habit
 * @param title The title of the habit
 * @param frequency Frequency schedule (DAILY, WEEKLY, MONTHLY)
 * @param currentStreak Current unbroken streak count
 * @param longestStreak All-time peak streak count
 * @param todayStatus Status for today ("DONE", "MISSED", "RECOVERED", or "PENDING")
 * @param notes Optional notes associated with today's log entry
 */
public record TodayHabitDTO(
        Long habitId,
        String title,
        Habit.Frequency frequency,
        int currentStreak,
        int longestStreak,
        String todayStatus,
        String notes
) {}
