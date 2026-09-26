package com.momentum.habittracker.Service;

import com.momentum.habittracker.DTO.HabitRequestDTO;
import com.momentum.habittracker.DTO.HabitResponseDTO;
import com.momentum.habittracker.Entities.Habit;
import com.momentum.habittracker.Entities.User;
import com.momentum.habittracker.Exception.ResourceNotFoundException;
import com.momentum.habittracker.Repository.HabitLogRepository;
import com.momentum.habittracker.Repository.HabitRepository;
import com.momentum.habittracker.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service managing user habit lifecycle operations (CRUD and archiving).
 *
 * Purpose:
 * Encapsulates all business logic relating to user-defined habits:
 * - Creating new habits bound to the authenticated user.
 * - Retrieving all habits or active/archived subsets for a user.
 * - Retrieving individual habit details ensuring strict ownership isolation.
 * - Updating habit metadata (title, description, cadence).
 * - Archiving/unarchiving habits without deleting historical streak data.
 * - Deleting habits and safely cleaning up associated logs.
 */
@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserRepository userRepository;

    /**
     * Constructs HabitService with required repository dependencies.
     *
     * @param habitRepository persistence repository for Habit entities
     * @param habitLogRepository persistence repository for HabitLog entities
     * @param userRepository persistence repository for User entities
     */
    public HabitService(HabitRepository habitRepository,
                        HabitLogRepository habitLogRepository,
                        UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new habit associated with the authenticated user.
     *
     * @param requestDTO validated payload containing habit title, description, and frequency
     * @param username username of the authenticated user
     * @return HabitResponseDTO of the created habit
     */
    @Transactional
    public HabitResponseDTO createHabit(HabitRequestDTO requestDTO, String username) {
        User user = findUserByUsername(username);

        Habit habit = Habit.builder()
                .title(requestDTO.title().trim())
                .description(requestDTO.description() != null ? requestDTO.description().trim() : null)
                .frequency(requestDTO.frequency())
                .active(true)
                .currentStreak(0)
                .longestStreak(0)
                .user(user)
                .build();

        Habit savedHabit = habitRepository.save(habit);
        return mapToDTO(savedHabit);
    }

    /**
     * Retrieves all habits belonging to the authenticated user, optionally filtered by active state.
     *
     * @param username username of the authenticated user
     * @param activeOnly optional boolean filter: true for active only, false for archived only, null for all
     * @return list of matching HabitResponseDTOs
     */
    @Transactional(readOnly = true)
    public List<HabitResponseDTO> getAllHabits(String username, Boolean activeOnly) {
        User user = findUserByUsername(username);

        List<Habit> habits;
        if (activeOnly != null) {
            habits = habitRepository.findByUserIdAndActive(user.getId(), activeOnly);
        } else {
            habits = habitRepository.findByUserId(user.getId());
        }

        return habits.stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Retrieves a single habit by ID, verifying ownership by the authenticated user.
     *
     * @param habitId primary key identifier of the habit
     * @param username username of the authenticated user
     * @return HabitResponseDTO of the requested habit
     * @throws ResourceNotFoundException if habit does not exist or belongs to another user
     */
    @Transactional(readOnly = true)
    public HabitResponseDTO getHabitById(Long habitId, String username) {
        Habit habit = findHabitByIdAndUsername(habitId, username);
        return mapToDTO(habit);
    }

    /**
     * Updates an existing habit's title, description, and frequency.
     *
     * @param habitId primary key identifier of the habit to update
     * @param requestDTO updated habit values
     * @param username username of the authenticated user
     * @return updated HabitResponseDTO
     */
    @Transactional
    public HabitResponseDTO updateHabit(Long habitId, HabitRequestDTO requestDTO, String username) {
        Habit habit = findHabitByIdAndUsername(habitId, username);

        habit.setTitle(requestDTO.title().trim());
        habit.setDescription(requestDTO.description() != null ? requestDTO.description().trim() : null);
        habit.setFrequency(requestDTO.frequency());

        Habit updatedHabit = habitRepository.save(habit);
        return mapToDTO(updatedHabit);
    }

    /**
     * Toggles the active/archived status of a habit.
     * Archiving pauses the habit without losing historical streak progress.
     *
     * @param habitId primary key identifier of the habit
     * @param username username of the authenticated user
     * @return updated HabitResponseDTO with toggled active state
     */
    @Transactional
    public HabitResponseDTO toggleHabitStatus(Long habitId, String username) {
        Habit habit = findHabitByIdAndUsername(habitId, username);
        habit.setActive(!habit.isActive());

        Habit updatedHabit = habitRepository.save(habit);
        return mapToDTO(updatedHabit);
    }

    /**
     * Permanently deletes a habit and all its associated check-in logs.
     *
     * @param habitId primary key identifier of the habit to delete
     * @param username username of the authenticated user
     */
    @Transactional
    public void deleteHabit(Long habitId, String username) {
        Habit habit = findHabitByIdAndUsername(habitId, username);

        // Delete associated logs first to preserve referential integrity
        habitLogRepository.deleteByHabitId(habit.getId());

        // Delete the habit
        habitRepository.delete(habit);
    }

    /**
     * Helper method to look up a User entity by username.
     *
     * @param username unique username
     * @return User entity
     * @throws ResourceNotFoundException if user does not exist
     */
    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Helper method to find a habit and verify that it belongs to the authenticated user.
     *
     * @param habitId habit ID
     * @param username username of the caller
     * @return Habit entity if found and authorized
     * @throws ResourceNotFoundException if not found or unauthorized
     */
    private Habit findHabitByIdAndUsername(Long habitId, String username) {
        User user = findUserByUsername(username);
        return habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Habit not found with id: " + habitId + " for current user"
                ));
    }

    /**
     * Helper method to convert a {@link Habit} entity into a {@link HabitResponseDTO}.
     *
     * @param habit the entity to convert
     * @return populated DTO
     */
    public HabitResponseDTO mapToDTO(Habit habit) {
        return new HabitResponseDTO(
                habit.getId(),
                habit.getTitle(),
                habit.getDescription(),
                habit.getFrequency(),
                habit.getEffectiveCurrentStreak(),
                habit.getEffectiveLongestStreak(),
                habit.getLastCompletedDate(),
                habit.isActive(),
                habit.getCreatedAt()
        );
    }
}
