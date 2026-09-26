package com.momentum.habittracker.Controller;

import com.momentum.habittracker.DTO.HabitLogRequestDTO;
import com.momentum.habittracker.DTO.HabitLogResponseDTO;
import com.momentum.habittracker.DTO.HabitStatsDTO;
import com.momentum.habittracker.Service.HabitLogService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for habit logging and streak inspection.
 *
 * Purpose:
 * Exposes endpoints allowing authenticated users to log daily check-ins for their habits,
 * retrieve log histories, inspect calendar ranges, delete logs, and view computed stats.
 * Base route: /api/habits/{habitId}/logs
 */
@RestController
@RequestMapping("/api/habits/{habitId}")
public class HabitLogController {

    private final HabitLogService habitLogService;

    /**
     * Constructs HabitLogController with required HabitLogService delegate.
     *
     * @param habitLogService service managing check-ins and streak metrics
     */
    public HabitLogController(HabitLogService habitLogService) {
        this.habitLogService = habitLogService;
    }

    /**
     * Records or updates a daily check-in log for a habit.
     *
     * Endpoint: POST /api/habits/{habitId}/logs
     * Access: Authenticated
     *
     * @param habitId primary key identifier of the habit
     * @param requestDTO validated check-in details (date, status, notes)
     * @param authentication current security context containing caller's username
     * @return 200 OK with the saved HabitLogResponseDTO
     */
    @PostMapping("/logs")
    public ResponseEntity<HabitLogResponseDTO> logHabit(
            @PathVariable Long habitId,
            @Valid @RequestBody HabitLogRequestDTO requestDTO,
            Authentication authentication) {
        HabitLogResponseDTO response = habitLogService.logHabit(habitId, requestDTO, authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all log entries for a habit, ordered newest to oldest.
     *
     * Endpoint: GET /api/habits/{habitId}/logs
     * Access: Authenticated
     *
     * @param habitId primary key identifier of the habit
     * @param authentication current security context
     * @return 200 OK with list of log entries
     */
    @GetMapping("/logs")
    public ResponseEntity<List<HabitLogResponseDTO>> getLogsForHabit(
            @PathVariable Long habitId,
            Authentication authentication) {
        List<HabitLogResponseDTO> logs = habitLogService.getLogsForHabit(habitId, authentication.getName());
        return ResponseEntity.ok(logs);
    }

    /**
     * Retrieves log entries within a specific date range (inclusive).
     *
     * Endpoint: GET /api/habits/{habitId}/logs/range?start=2026-09-01&end=2026-09-30
     * Access: Authenticated
     *
     * @param habitId primary key identifier of the habit
     * @param start start date in ISO format (YYYY-MM-DD)
     * @param end end date in ISO format (YYYY-MM-DD)
     * @param authentication current security context
     * @return 200 OK with list of logs within the range
     */
    @GetMapping("/logs/range")
    public ResponseEntity<List<HabitLogResponseDTO>> getLogsByDateRange(
            @PathVariable Long habitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Authentication authentication) {
        List<HabitLogResponseDTO> logs = habitLogService.getLogsByDateRange(
                habitId, start, end, authentication.getName()
        );
        return ResponseEntity.ok(logs);
    }

    /**
     * Deletes a specific habit check-in log and recalculates streaks.
     *
     * Endpoint: DELETE /api/habits/{habitId}/logs/{logId}
     * Access: Authenticated
     *
     * @param habitId primary key identifier of the parent habit
     * @param logId primary key identifier of the log entry to remove
     * @param authentication current security context
     * @return 204 No Content
     */
    @DeleteMapping("/logs/{logId}")
    public ResponseEntity<Void> deleteLog(
            @PathVariable Long habitId,
            @PathVariable Long logId,
            Authentication authentication) {
        habitLogService.deleteLog(habitId, logId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves aggregated performance stats and streak analytics for a habit.
     *
     * Endpoint: GET /api/habits/{habitId}/stats
     * Access: Authenticated
     *
     * @param habitId primary key identifier of the habit
     * @param authentication current security context
     * @return 200 OK with habit performance metrics
     */
    @GetMapping("/stats")
    public ResponseEntity<HabitStatsDTO> getHabitStats(
            @PathVariable Long habitId,
            Authentication authentication) {
        HabitStatsDTO stats = habitLogService.getHabitStats(habitId, authentication.getName());
        return ResponseEntity.ok(stats);
    }
}
