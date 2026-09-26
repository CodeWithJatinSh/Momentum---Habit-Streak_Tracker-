package com.momentum.habittracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Momentum - Habit & Streak Tracker application.
 *
 * Purpose:
 * Bootstraps the Spring Boot application context, enabling auto-configuration,
 * component scanning across sub-packages, JPA repository registration, and
 * embedded web server startup.
 */
@SpringBootApplication
public class MomentumApplication {

    /**
     * Application execution entry point.
     *
     * @param args command-line arguments passed at startup
     */
    public static void main(String[] args) {
        SpringApplication.run(MomentumApplication.class, args);
    }

}
