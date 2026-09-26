package com.momentum.habittracker.Service;

import com.momentum.habittracker.DTO.DashboardResponseDTO;
import com.momentum.habittracker.DTO.HabitLogResponseDTO;
import com.momentum.habittracker.DTO.TodayHabitDTO;
import com.momentum.habittracker.Entities.Habit;
import com.momentum.habittracker.Entities.HabitLog;
import com.momentum.habittracker.Entities.User;
import com.momentum.habittracker.Exception.ResourceNotFoundException;
import com.momentum.habittracker.Repository.HabitLogRepository;
import com.momentum.habittracker.Repository.HabitRepository;
import com.momentum.habittracker.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service orchestrating dashboard metrics and user activity aggregations.
 *
 * Purpose:
 * Consolidates data across habits and logs into a single high-performance summary:
 * - Active vs total habit counters.
 * - Daily check-in progress (completed, pending, completion rate).
 * - Peak active streaks and all-time longest streaks.
 * - Real-time habit status mapping for today's interactive checklist.
 * - Recent check-in activity stream.
 */
@Service
public class DashboardService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserRepository userRepository;

    /**
     * Constructs DashboardService with required repository dependencies.
     */
    public DashboardService(HabitRepository habitRepository,
                            HabitLogRepository habitLogRepository,
                            UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * Generates a comprehensive dashboard view for the authenticated user.
     *
     * @param username username of the caller
     * @return DashboardResponseDTO with all computed KPIs and daily statuses
     */
    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        List<Habit> allHabits = habitRepository.findByUserId(user.getId());
        List<Habit> activeHabits = allHabits.stream()
                .filter(Habit::isActive)
                .toList();

        LocalDate today = LocalDate.now();

        // Fetch all logs recorded today for this user
        List<HabitLog> todayLogs = habitLogRepository.findByHabitUserIdAndDate(user.getId(), today);
        Map<Long, HabitLog> todayLogsByHabitId = todayLogs.stream()
                .collect(Collectors.toMap(l -> l.getHabit().getId(), l -> l, (a, b) -> a));

        // Count active habits completed today (DONE or RECOVERED)
        long completedToday = activeHabits.stream()
                .filter(h -> {
                    HabitLog log = todayLogsByHabitId.get(h.getId());
                    return log != null && (log.getStatus() == HabitLog.Status.DONE || log.getStatus() == HabitLog.Status.RECOVERED);
                })
                .count();

        int totalHabitsCount = allHabits.size();
        int activeHabitsCount = activeHabits.size();
        int pendingToday = Math.max(0, activeHabitsCount - (int) completedToday);

        double completionRate = activeHabitsCount == 0
                ? 0.0
                : Math.round(((double) completedToday / activeHabitsCount) * 1000.0) / 10.0;

        int longestActiveStreak = activeHabits.stream()
                .mapToInt(Habit::getEffectiveCurrentStreak)
                .max()
                .orElse(0);

        int bestAllTimeStreak = allHabits.stream()
                .mapToInt(Habit::getEffectiveLongestStreak)
                .max()
                .orElse(0);

        // Build list of interactive habit cards for today
        List<TodayHabitDTO> todayHabits = activeHabits.stream()
                .map(habit -> {
                    HabitLog log = todayLogsByHabitId.get(habit.getId());
                    String status = (log != null) ? log.getStatus().name() : "PENDING";
                    String notes = (log != null) ? log.getNotes() : null;

                    return new TodayHabitDTO(
                            habit.getId(),
                            habit.getTitle(),
                            habit.getFrequency(),
                            habit.getEffectiveCurrentStreak(),
                            habit.getEffectiveLongestStreak(),
                            status,
                            notes
                    );
                })
                .toList();

        // Fetch top 10 most recent activity logs across all user habits
        List<HabitLogResponseDTO> recentActivity = habitLogRepository.findByHabitUserIdOrderByDateDesc(user.getId())
                .stream()
                .limit(10)
                .map(this::mapLogToDTO)
                .toList();

        return new DashboardResponseDTO(
                totalHabitsCount,
                activeHabitsCount,
                (int) completedToday,
                pendingToday,
                completionRate,
                longestActiveStreak,
                bestAllTimeStreak,
                todayHabits,
                recentActivity
        );
    }

    /**
     * Helper to map a {@link HabitLog} entity to {@link HabitLogResponseDTO}.
     */
    private HabitLogResponseDTO mapLogToDTO(HabitLog log) {
        return new HabitLogResponseDTO(
                log.getId(),
                log.getHabit().getId(),
                log.getDate(),
                log.getStatus(),
                log.getNotes(),
                log.getLoggedAt()
        );
    }
}
