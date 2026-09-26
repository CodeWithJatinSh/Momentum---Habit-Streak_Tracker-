package com.momentum.habittracker.DTO;

import java.util.List;

/**
 * Data Transfer Object representing the user's high-level dashboard metrics.
 *
 * Purpose:
 * Aggregates all daily KPI metrics, active habit check-in states, and recent activity
 * in a single unified payload for dashboard visualization.
 *
 * @param totalHabits Total number of user habits
 * @param activeHabits Number of currently active habits
 * @param completedToday Number of habits marked DONE or RECOVERED today
 * @param pendingToday Number of active habits still waiting to be logged today
 * @param todayCompletionRate Percentage of active habits completed today (0.0 - 100.0)
 * @param longestActiveStreak Highest active streak across all active habits
 * @param bestAllTimeStreak Highest streak ever achieved across all habits
 * @param todayHabits List of daily habit cards with current completion status
 * @param recentActivity List of recent check-in logs across all habits
 */
public record DashboardResponseDTO(
        int totalHabits,
        int activeHabits,
        int completedToday,
        int pendingToday,
        double todayCompletionRate,
        int longestActiveStreak,
        int bestAllTimeStreak,
        List<TodayHabitDTO> todayHabits,
        List<HabitLogResponseDTO> recentActivity
) {}
