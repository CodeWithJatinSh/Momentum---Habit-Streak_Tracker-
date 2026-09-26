package com.momentum.habittracker.Repository;

import com.momentum.habittracker.Entities.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Habit} entity operations.
 */
@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {

    /**
     * Retrieves all habits belonging to a specific user.
     *
     * @param userId the ID of the user
     * @return list of habits associated with the user
     */
    List<Habit> findByUserId(Long userId);

    /**
     * Retrieves all habits belonging to a user filtered by active/archived status.
     *
     * @param userId the ID of the user
     * @param active true for active habits, false for archived habits
     * @return list of matching habits
     */
    List<Habit> findByUserIdAndActive(Long userId, boolean active);

    /**
     * Retrieves a habit by its ID and the owner user ID.
     * Ensures users can only access or modify their own habits.
     *
     * @param id the habit ID
     * @param userId the user ID of the owner
     * @return an Optional containing the Habit if found and owned by the user, or empty
     */
    Optional<Habit> findByIdAndUserId(Long id, Long userId);
}
