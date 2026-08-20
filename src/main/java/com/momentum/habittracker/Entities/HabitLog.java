package com.momentum.habittracker.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "habit_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_log_id", nullable = false)
    private Long id;

    private LocalDate Date;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    private enum Status{
        DONE,MISSED,RECOVERED
    }
}