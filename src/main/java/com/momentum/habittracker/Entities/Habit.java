package com.momentum.habittracker.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "habit")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "habit_id", nullable = false)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private Habit.Frequency frequency;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Lifecycle hook — runs automatically before insert
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Enum defined inside the entity, as a nested type
    public enum Frequency {
        DAILY, WEEKLY, MONTHLY
    }

}