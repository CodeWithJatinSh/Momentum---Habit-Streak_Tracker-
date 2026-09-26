package com.momentum.habittracker.DTO;

import java.time.LocalDate;

/**
 * Data Transfer Object representing aggregated analytics and performance statistics for a habit.
 *
 * Purpose:
 * Computes and exposes key progress metrics for a habit: total completions, misses,
 * completion percentage rate, current active streak, and all-time longest streak.
 *
 * @param habitId The primary key identifier of the habit
 * @param totalLogs Total number of recorded check-ins
 * @param totalCompletions Total number of successful completions (DONE + RECOVERED)
 * @param totalMissed Total number of missed days
 * @param completionRate Percentage of completions relative to total logs (0.0 to 100.0)
 * @param currentStreak The current active unbroken streak count
 * @param longestStreak The all-time highest streak count
 * @param lastCompletedDate The date of the most recent completion
 */
public record HabitStatsDTO(
        Long habitId,
        long totalLogs,
        long totalCompletions,
        long totalMissed,
        double completionRate,
        int currentStreak,
        int longestStreak,
        LocalDate lastCompletedDate
) {}
