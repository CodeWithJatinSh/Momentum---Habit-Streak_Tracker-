package com.momentum.habittracker.Controller;

import com.momentum.habittracker.DTO.DashboardResponseDTO;
import com.momentum.habittracker.Service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for dashboard data aggregation.
 *
 * Purpose:
 * Exposes a high-level endpoint consolidating all daily metrics, today's habit check-in
 * checklist, streaks, and recent activities for the authenticated user.
 * Base route: /api/dashboard
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Constructs DashboardController with required DashboardService delegate.
     */
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Retrieves unified dashboard analytics and daily check-in checklist.
     *
     * Endpoint: GET /api/dashboard
     * Access: Authenticated (Bearer JWT required)
     *
     * @param authentication current security context containing caller's username
     * @return 200 OK with DashboardResponseDTO
     */
    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard(Authentication authentication) {
        DashboardResponseDTO dashboard = dashboardService.getDashboard(authentication.getName());
        return ResponseEntity.ok(dashboard);
    }
}
