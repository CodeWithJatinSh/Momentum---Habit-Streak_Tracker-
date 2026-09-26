package com.momentum.habittracker.Service;

import com.momentum.habittracker.DTO.HabitLogRequestDTO;
import com.momentum.habittracker.DTO.HabitLogResponseDTO;
import com.momentum.habittracker.DTO.HabitStatsDTO;
import com.momentum.habittracker.Entities.Habit;
import com.momentum.habittracker.Entities.HabitLog;
import com.momentum.habittracker.Entities.User;
import com.momentum.habittracker.Exception.ResourceNotFoundException;
import com.momentum.habittracker.Repository.HabitLogRepository;
import com.momentum.habittracker.Repository.HabitRepository;
import com.momentum.habittracker.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing habit execution logs and streak calculation engine.
 *
 * Purpose:
 * Centralizes all check-in interactions and mathematical streak recalculations:
 * 1. Recording / updating daily check-in logs (DONE, MISSED, RECOVERED).
 * 2. Deterministic streak calculation engine (currentStreak, longestStreak, lastCompletedDate)
 *    supporting DAILY, WEEKLY, and MONTHLY frequencies.
 * 3. Log history retrieval for calendar views, heatmaps, and date-range filters.
 * 4. Aggregated habit performance statistics (completion rate, total completions).
 */
@Service
public class HabitLogService {

    private final HabitLogRepository habitLogRepository;
    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    /**
     * Constructs HabitLogService with necessary persistence repositories.
     *
     * @param habitLogRepository persistence repository for HabitLog entities
     * @param habitRepository persistence repository for Habit entities
     * @param userRepository persistence repository for User entities
     */
    public HabitLogService(HabitLogRepository habitLogRepository,
                           HabitRepository habitRepository,
                           UserRepository userRepository) {
        this.habitLogRepository = habitLogRepository;
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    /**
     * Records or updates a daily check-in log for a specific habit.
     * If a log already exists for the given date, its status and notes are updated.
     * Automatically triggers streak recalculation upon saving.
     *
     * @param habitId primary key of the target habit
     * @param requestDTO validated check-in data (date, status, notes)
     * @param username username of the authenticated user
     * @return HabitLogResponseDTO containing the saved log details
     * @throws IllegalArgumentException if the log date is in the future
     * @throws ResourceNotFoundException if the habit does not exist or belongs to another user
     */
    @Transactional
    public HabitLogResponseDTO logHabit(Long habitId, HabitLogRequestDTO requestDTO, String username) {
        if (requestDTO.date().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cannot log habit for future dates: " + requestDTO.date());
        }

        Habit habit = findHabitByIdAndUsername(habitId, username);

        // Check if an entry for this habit on this date already exists
        Optional<HabitLog> existingLogOpt = habitLogRepository.findByHabitIdAndDate(habit.getId(), requestDTO.date());

        HabitLog logToSave;
        if (existingLogOpt.isPresent()) {
            logToSave = existingLogOpt.get();
            logToSave.setStatus(requestDTO.status());
            logToSave.setNotes(requestDTO.notes());
        } else {
            logToSave = HabitLog.builder()
                    .habit(habit)
                    .date(requestDTO.date())
                    .status(requestDTO.status())
                    .notes(requestDTO.notes())
                    .build();
        }

        HabitLog savedLog = habitLogRepository.save(logToSave);

        // Recalculate streak metrics for the habit
        recalculateStreaks(habit);

        return mapToDTO(savedLog);
    }

    /**
     * Retrieves all log entries for a habit, sorted newest to oldest.
     *
     * @param habitId primary key of the target habit
     * @param username username of the authenticated user
     * @return list of HabitLogResponseDTOs
     */
    @Transactional(readOnly = true)
    public List<HabitLogResponseDTO> getLogsForHabit(Long habitId, String username) {
        Habit habit = findHabitByIdAndUsername(habitId, username);
        return habitLogRepository.findByHabitIdOrderByDateDesc(habit.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Retrieves log entries for a habit within a specified date range, sorted chronologically.
     * Ideal for calendar grids, weekly views, and contribution heatmaps.
     *
     * @param habitId primary key of the target habit
     * @param startDate starting date (inclusive)
     * @param endDate ending date (inclusive)
     * @param username username of the authenticated user
     * @return list of HabitLogResponseDTOs in chronological order
     * @throws IllegalArgumentException if startDate is after endDate
     */
    @Transactional(readOnly = true)
    public List<HabitLogResponseDTO> getLogsByDateRange(Long habitId, LocalDate startDate, LocalDate endDate, String username) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        Habit habit = findHabitByIdAndUsername(habitId, username);
        return habitLogRepository.findByHabitIdAndDateBetweenOrderByDateAsc(habit.getId(), startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Deletes a specific log entry and recalculates streaks for the habit.
     *
     * @param habitId primary key of the parent habit
     * @param logId primary key of the log entry to remove
     * @param username username of the authenticated user
     * @throws ResourceNotFoundException if the log entry does not exist or does not belong to this habit
     */
    @Transactional
    public void deleteLog(Long habitId, Long logId, String username) {
        Habit habit = findHabitByIdAndUsername(habitId, username);

        HabitLog log = habitLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Habit log not found with id: " + logId));

        if (!log.getHabit().getId().equals(habit.getId())) {
            throw new ResourceNotFoundException("Log id " + logId + " does not belong to habit id " + habitId);
        }

        habitLogRepository.delete(log);

        // Recalculate streak after deletion
        recalculateStreaks(habit);
    }

    /**
     * Computes aggregated performance statistics and historical metrics for a habit.
     *
     * @param habitId primary key of the habit
     * @param username username of the authenticated user
     * @return HabitStatsDTO containing totals, streaks, and completion rate
     */
    @Transactional(readOnly = true)
    public HabitStatsDTO getHabitStats(Long habitId, String username) {
        Habit habit = findHabitByIdAndUsername(habitId, username);

        List<HabitLog> logs = habitLogRepository.findByHabitIdOrderByDateDesc(habit.getId());

        long totalLogs = logs.size();
        long totalCompletions = logs.stream()
                .filter(l -> l.getStatus() == HabitLog.Status.DONE || l.getStatus() == HabitLog.Status.RECOVERED)
                .count();
        long totalMissed = logs.stream()
                .filter(l -> l.getStatus() == HabitLog.Status.MISSED)
                .count();

        double completionRate = totalLogs == 0
                ? 0.0
                : Math.round(((double) totalCompletions / totalLogs) * 1000.0) / 10.0;

        return new HabitStatsDTO(
                habit.getId(),
                totalLogs,
                totalCompletions,
                totalMissed,
                completionRate,
                habit.getEffectiveCurrentStreak(),
                habit.getEffectiveLongestStreak(),
                habit.getLastCompletedDate()
        );
    }

    /**
     * Deterministic streak calculation engine.
     * Computes current unbroken streak, all-time longest streak, and last completed date
     * tailored to the habit's frequency (DAILY, WEEKLY, MONTHLY).
     *
     * @param habit the habit entity to update
     */
    public void recalculateStreaks(Habit habit) {
        List<HabitLog> allLogs = habitLogRepository.findByHabitIdOrderByDateDesc(habit.getId());

        // Extract dates marked DONE or RECOVERED
        TreeSet<LocalDate> completedDates = allLogs.stream()
                .filter(l -> l.getStatus() == HabitLog.Status.DONE || l.getStatus() == HabitLog.Status.RECOVERED)
                .map(HabitLog::getDate)
                .collect(Collectors.toCollection(TreeSet::new));

        LocalDate lastCompletedDate = completedDates.isEmpty() ? null : completedDates.last();
        habit.setLastCompletedDate(lastCompletedDate);

        if (completedDates.isEmpty()) {
            habit.setCurrentStreak(0);
            habit.setLongestStreak(0);
            habitRepository.save(habit);
            return;
        }

        LocalDate today = LocalDate.now();
        int currentStreak;
        int longestStreak;

        switch (habit.getFrequency()) {
            case WEEKLY -> {
                LocalDate currentWeekMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                boolean currentWeekMissed = allLogs.stream()
                        .anyMatch(l -> l.getDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).equals(currentWeekMonday)
                                && l.getStatus() == HabitLog.Status.MISSED);
                currentStreak = computeWeeklyCurrentStreak(completedDates, currentWeekMissed);
                longestStreak = computeWeeklyLongestStreak(completedDates);
            }
            case MONTHLY -> {
                YearMonth currentMonth = YearMonth.now();
                boolean currentMonthMissed = allLogs.stream()
                        .anyMatch(l -> YearMonth.from(l.getDate()).equals(currentMonth)
                                && l.getStatus() == HabitLog.Status.MISSED);
                currentStreak = computeMonthlyCurrentStreak(completedDates, currentMonthMissed);
                longestStreak = computeMonthlyLongestStreak(completedDates);
            }
            default -> { // DAILY
                boolean todayMissed = allLogs.stream()
                        .anyMatch(l -> l.getDate().equals(today) && l.getStatus() == HabitLog.Status.MISSED);
                currentStreak = computeDailyCurrentStreak(completedDates, todayMissed);
                longestStreak = computeDailyLongestStreak(completedDates);
            }
        }

        habit.setCurrentStreak(currentStreak);
        habit.setLongestStreak(longestStreak);
        habitRepository.save(habit);
    }

    /**
     * Calculates current active daily streak.
     * A daily streak is considered active if today or yesterday was completed,
     * unless today was explicitly marked as MISSED.
     */
    private int computeDailyCurrentStreak(TreeSet<LocalDate> completedDates, boolean todayMissed) {
        if (todayMissed) {
            return 0; // Today was explicitly marked missed; active streak is broken
        }

        LocalDate today = LocalDate.now();
        LocalDate checkDate;

        if (completedDates.contains(today)) {
            checkDate = today;
        } else if (completedDates.contains(today.minusDays(1))) {
            checkDate = today.minusDays(1);
        } else {
            return 0; // Streak broken
        }

        int streak = 0;
        while (completedDates.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        return streak;
    }

    /**
     * Calculates all-time longest consecutive daily streak across the entire log history.
     */
    private int computeDailyLongestStreak(TreeSet<LocalDate> completedDates) {
        int longest = 0;
        int currentRun = 0;
        LocalDate previous = null;

        for (LocalDate date : completedDates) {
            if (previous != null && date.equals(previous.plusDays(1))) {
                currentRun++;
            } else {
                currentRun = 1;
            }
            if (currentRun > longest) {
                longest = currentRun;
            }
            previous = date;
        }

        return longest;
    }

    /**
     * Calculates current weekly streak based on consecutive completed calendar weeks (Monday-aligned).
     */
    private int computeWeeklyCurrentStreak(TreeSet<LocalDate> completedDates, boolean currentWeekMissed) {
        if (currentWeekMissed) {
            return 0; // Current week was explicitly marked missed
        }

        TreeSet<LocalDate> completedWeeks = new TreeSet<>();
        for (LocalDate date : completedDates) {
            completedWeeks.add(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        }

        LocalDate currentWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate checkWeek;

        if (completedWeeks.contains(currentWeek)) {
            checkWeek = currentWeek;
        } else if (completedWeeks.contains(currentWeek.minusWeeks(1))) {
            checkWeek = currentWeek.minusWeeks(1);
        } else {
            return 0;
        }

        int streak = 0;
        while (completedWeeks.contains(checkWeek)) {
            streak++;
            checkWeek = checkWeek.minusWeeks(1);
        }
        return streak;
    }

    /**
     * Calculates all-time longest weekly streak.
     */
    private int computeWeeklyLongestStreak(TreeSet<LocalDate> completedDates) {
        TreeSet<LocalDate> completedWeeks = new TreeSet<>();
        for (LocalDate date : completedDates) {
            completedWeeks.add(date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)));
        }

        int longest = 0;
        int currentRun = 0;
        LocalDate previous = null;

        for (LocalDate week : completedWeeks) {
            if (previous != null && week.equals(previous.plusWeeks(1))) {
                currentRun++;
            } else {
                currentRun = 1;
            }
            if (currentRun > longest) {
                longest = currentRun;
            }
            previous = week;
        }

        return longest;
    }

    /**
     * Calculates current monthly streak based on consecutive completed calendar months.
     */
    private int computeMonthlyCurrentStreak(TreeSet<LocalDate> completedDates, boolean currentMonthMissed) {
        if (currentMonthMissed) {
            return 0; // Current month was explicitly marked missed
        }

        TreeSet<YearMonth> completedMonths = new TreeSet<>();
        for (LocalDate date : completedDates) {
            completedMonths.add(YearMonth.from(date));
        }

        YearMonth currentMonth = YearMonth.now();
        YearMonth checkMonth;

        if (completedMonths.contains(currentMonth)) {
            checkMonth = currentMonth;
        } else if (completedMonths.contains(currentMonth.minusMonths(1))) {
            checkMonth = currentMonth.minusMonths(1);
        } else {
            return 0;
        }

        int streak = 0;
        while (completedMonths.contains(checkMonth)) {
            streak++;
            checkMonth = checkMonth.minusMonths(1);
        }
        return streak;
    }

    /**
     * Calculates all-time longest monthly streak.
     */
    private int computeMonthlyLongestStreak(TreeSet<LocalDate> completedDates) {
        TreeSet<YearMonth> completedMonths = new TreeSet<>();
        for (LocalDate date : completedDates) {
            completedMonths.add(YearMonth.from(date));
        }

        int longest = 0;
        int currentRun = 0;
        YearMonth previous = null;

        for (YearMonth month : completedMonths) {
            if (previous != null && month.equals(previous.plusMonths(1))) {
                currentRun++;
            } else {
                currentRun = 1;
            }
            if (currentRun > longest) {
                longest = currentRun;
            }
            previous = month;
        }

        return longest;
    }

    /**
     * Helper method to find a habit and verify that it belongs to the authenticated user.
     */
    private Habit findHabitByIdAndUsername(Long habitId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return habitRepository.findByIdAndUserId(habitId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Habit not found with id: " + habitId + " for current user"
                ));
    }

    /**
     * Maps a {@link HabitLog} entity to {@link HabitLogResponseDTO}.
     */
    private HabitLogResponseDTO mapToDTO(HabitLog log) {
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
