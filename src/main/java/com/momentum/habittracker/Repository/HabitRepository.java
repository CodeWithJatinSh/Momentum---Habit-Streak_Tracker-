package com.momentum.habittracker.Repository;

import com.momentum.habittracker.Entities.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitRepository extends JpaRepository<Habit, Long> {
}
