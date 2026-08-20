package com.momentum.habittracker.Repository;

import com.momentum.habittracker.Entities.HabitLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
}