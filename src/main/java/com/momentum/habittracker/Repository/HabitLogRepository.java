package com.momentum.habittracker.Repository;

import com.momentum.habittracker.Entities.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link HabitLog} entity operations.
 */
@Repository
public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {

    /**
     * Finds a log entry for a specific habit on a given date.
     *
     * @param habitId the ID of the habit
     * @param date the date to check
     * @return an Optional containing the HabitLog if present, or empty
     */
    Optional<HabitLog> findByHabitIdAndDate(Long habitId, LocalDate date);

    /**
     * Retrieves all log entries for a habit, ordered from newest to oldest.
     * Useful for history inspection and streak computation.
     *
     * @param habitId the ID of the habit
     * @return list of logs in reverse chronological order
     */
    List<HabitLog> findByHabitIdOrderByDateDesc(Long habitId);

    /**
     * Retrieves all log entries for a habit within a specified date range, ordered chronologically.
     * Useful for calendar views, heatmaps, and weekly/monthly reports.
     *
     * @param habitId the ID of the habit
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of logs within the date range
     */
    List<HabitLog> findByHabitIdAndDateBetweenOrderByDateAsc(Long habitId, LocalDate startDate, LocalDate endDate);


    /**
     * Deletes all log entries associated with a given habit ID.
     * Ensures clean cascading deletion of logs when a habit is removed.
     *
     * @param habitId the ID of the habit
     */
    void deleteByHabitId(Long habitId);

    /**
     * Retrieves all log entries for a user across all habits on a specific date.
     *
     * @param userId the ID of the user
     * @param date the target date
     * @return list of log entries for the user on that date
     */
    List<HabitLog> findByHabitUserIdAndDate(Long userId, LocalDate date);

    /**
     * Retrieves all log entries for a user across all habits, ordered newest to oldest.
     *
     * @param userId the ID of the user
     * @return list of logs for the user in reverse chronological order
     */
    List<HabitLog> findByHabitUserIdOrderByDateDesc(Long userId);
}