package com.momentum.habittracker.Entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "User_id", nullable = false)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

}