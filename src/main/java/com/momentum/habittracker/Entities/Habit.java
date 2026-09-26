package com.momentum.habittracker.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a habit tracked by a user.
 * Stores habit details, frequency target, and current/longest streak stats.
 */
@Getter
@Setter
@Entity
@Table(name = "habit")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habit {

    /**
     * Primary key identifier for the habit.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_id", nullable = false)
    private Long id;

    /**
     * Short descriptive title of the habit (e.g., "Read 20 pages", "Workout").
     */
    @Column(nullable = false)
    private String title;

    /**
     * Optional detailed description or motivation for the habit.
     */
    private String description;

    /**
     * Target frequency schedule (DAILY, WEEKLY, MONTHLY).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    /**
     * Current consecutive streak count of completions.
     */
    @Builder.Default
    private int currentStreak = 0;

    /**
     * Longest consecutive streak ever achieved for this habit.
     */
    @Builder.Default
    private int longestStreak = 0;

    /**
     * Date of the most recent completion, used for calculating streak validity.
     */
    private LocalDate lastCompletedDate;

    /**
     * Flag indicating whether the habit is actively tracked (false = archived).
     */
    @Builder.Default
    private boolean active = true;

    /**
     * Timestamp when the habit was created.
     */
    private LocalDateTime createdAt;

    /**
     * The user who owns this habit. Lazily fetched for performance.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Lifecycle hook executed automatically before persisting a new habit.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Computes the real-time active streak considering elapsed time since the last completion.
     * Prevents displaying stale streak numbers when days/weeks have passed without check-ins.
     *
     * @return current active streak count, or 0 if expired
     */
    public int getEffectiveCurrentStreak() {
        if (this.lastCompletedDate == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();

        switch (this.frequency) {
            case WEEKLY -> {
                LocalDate startOfThisWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                LocalDate startOfLastWeek = startOfThisWeek.minusWeeks(1);
                LocalDate startOfLastCompletedWeek = this.lastCompletedDate.with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
                if (startOfLastCompletedWeek.isBefore(startOfLastWeek)) {
                    return 0;
                }
            }
            case MONTHLY -> {
                java.time.YearMonth currentMonth = java.time.YearMonth.now();
                java.time.YearMonth lastMonth = currentMonth.minusMonths(1);
                java.time.YearMonth completedMonth = java.time.YearMonth.from(this.lastCompletedDate);
                if (completedMonth.isBefore(lastMonth)) {
                    return 0;
                }
            }
            default -> { // DAILY
                if (this.lastCompletedDate.isBefore(today.minusDays(1))) {
                    return 0;
                }
            }
        }

        return this.currentStreak;
    }

    /**
     * Computes the real-time longest streak.
     * If the habit has never been completed (lastCompletedDate is null), the longest streak is 0.
     *
     * @return all-time peak streak count, or 0 if never completed
     */
    public int getEffectiveLongestStreak() {
        if (this.lastCompletedDate == null) {
            return 0;
        }
        return this.longestStreak;
    }

    /**
     * Frequency cadence for the habit.
     */
    public enum Frequency {
        DAILY, WEEKLY, MONTHLY
    }
}