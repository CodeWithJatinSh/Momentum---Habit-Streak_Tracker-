package com.momentum.habittracker.Controller;

import com.momentum.habittracker.DTO.HabitRequestDTO;
import com.momentum.habittracker.DTO.HabitResponseDTO;
import com.momentum.habittracker.Service.HabitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for habit management operations.
 *
 * Purpose:
 * Exposes secured endpoints allowing authenticated users to create, view, update,
 * toggle archive status, and delete their own habits.
 * Base route: /api/habits
 */
@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    /**
     * Constructs HabitController with required HabitService delegate.
     *
     * @param habitService business logic service for habits
     */
    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    /**
     * Creates a new habit for the authenticated user.
     *
     * Endpoint: POST /api/habits
     * Access: Authenticated
     *
     * @param requestDTO validated habit payload (title, description, frequency)
     * @param authentication current security context containing caller's username
     * @return 201 Created with the saved HabitResponseDTO
     */
    @PostMapping
    public ResponseEntity<HabitResponseDTO> createHabit(
            @Valid @RequestBody HabitRequestDTO requestDTO,
            Authentication authentication) {
        HabitResponseDTO response = habitService.createHabit(requestDTO, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves all habits belonging to the authenticated user.
     * Optionally filters by active or archived status.
     *
     * Endpoint: GET /api/habits?active=true
     * Access: Authenticated
     *
     * @param active optional filter flag (true = active, false = archived, omitted = all)
     * @param authentication current security context
     * @return 200 OK with list of habits
     */
    @GetMapping
    public ResponseEntity<List<HabitResponseDTO>> getAllHabits(
            @RequestParam(required = false) Boolean active,
            Authentication authentication) {
        List<HabitResponseDTO> habits = habitService.getAllHabits(authentication.getName(), active);
        return ResponseEntity.ok(habits);
    }

    /**
     * Retrieves a single habit by ID for the authenticated user.
     *
     * Endpoint: GET /api/habits/{id}
     * Access: Authenticated
     *
     * @param id primary key identifier of the habit
     * @param authentication current security context
     * @return 200 OK with the habit details
     */
    @GetMapping("/{id}")
    public ResponseEntity<HabitResponseDTO> getHabitById(
            @PathVariable Long id,
            Authentication authentication) {
        HabitResponseDTO habit = habitService.getHabitById(id, authentication.getName());
        return ResponseEntity.ok(habit);
    }

    /**
     * Updates an existing habit's title, description, and frequency.
     *
     * Endpoint: PUT /api/habits/{id}
     * Access: Authenticated
     *
     * @param id primary key identifier of the habit to update
     * @param requestDTO updated habit values
     * @param authentication current security context
     * @return 200 OK with the updated HabitResponseDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<HabitResponseDTO> updateHabit(
            @PathVariable Long id,
            @Valid @RequestBody HabitRequestDTO requestDTO,
            Authentication authentication) {
        HabitResponseDTO updatedHabit = habitService.updateHabit(id, requestDTO, authentication.getName());
        return ResponseEntity.ok(updatedHabit);
    }

    /**
     * Toggles the active/archived status of a habit.
     *
     * Endpoint: PATCH /api/habits/{id}/toggle-status
     * Access: Authenticated
     *
     * @param id primary key identifier of the habit
     * @param authentication current security context
     * @return 200 OK with the updated habit status
     */
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<HabitResponseDTO> toggleHabitStatus(
            @PathVariable Long id,
            Authentication authentication) {
        HabitResponseDTO habit = habitService.toggleHabitStatus(id, authentication.getName());
        return ResponseEntity.ok(habit);
    }

    /**
     * Deletes a habit and all associated logs permanently.
     *
     * Endpoint: DELETE /api/habits/{id}
     * Access: Authenticated
     *
     * @param id primary key identifier of the habit to delete
     * @param authentication current security context
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(
            @PathVariable Long id,
            Authentication authentication) {
        habitService.deleteHabit(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
