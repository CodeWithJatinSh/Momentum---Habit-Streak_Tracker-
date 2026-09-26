package com.momentum.habittracker.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a daily execution log for a specific habit.
 * Records the status of the habit on a given date to compute streaks and history.
 */
@Getter
@Setter
@Entity
@Table(name = "habit_log", uniqueConstraints = {
    @UniqueConstraint(name = "uk_habit_date", columnNames = {"habit_id", "date"})
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitLog {

    /**
     * Primary key identifier for the habit log entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_log_id", nullable = false)
    private Long id;

    /**
     * Date for which this log is recorded.
     * Guaranteed unique per habit via the uk_habit_date constraint.
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * Completion status for the habit on this date (DONE, MISSED, RECOVERED).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    /**
     * Optional user notes or reflections for this completion.
     */
    private String notes;

    /**
     * Timestamp when this log entry was recorded.
     */
    private LocalDateTime loggedAt;

    /**
     * The habit associated with this log entry. Lazily fetched.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    /**
     * Lifecycle hook executed automatically before saving a log entry.
     */
    @PrePersist
    protected void onLog() {
        if (this.loggedAt == null) {
            this.loggedAt = LocalDateTime.now();
        }
    }

    /**
     * Possible completion statuses for a habit log entry.
     */
    public enum Status {
        DONE,       // Successfully completed the habit
        MISSED,     // Failed/missed the habit for the day
        RECOVERED   // Recovered using a streak freeze or grace period
    }
}