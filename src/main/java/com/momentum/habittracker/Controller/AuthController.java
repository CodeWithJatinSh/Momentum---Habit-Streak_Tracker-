package com.momentum.habittracker.Controller;

import com.momentum.habittracker.DTO.AuthResponseDTO;
import com.momentum.habittracker.DTO.UserLoginDTO;
import com.momentum.habittracker.DTO.UserRegisterDTO;
import com.momentum.habittracker.DTO.UserResponseDTO;
import com.momentum.habittracker.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller managing authentication operations.
 *
 * Purpose:
 * Exposes public HTTP endpoints for user registration and login, as well as a secured
 * endpoint to inspect the currently logged-in user's profile.
 * Base route: /api/auth
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructs AuthController with the AuthService business logic delegate.
     *
     * @param authService service handling registration, login, and user profile
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user account.
     *
     * Endpoint: POST /api/auth/register
     * Access: Public
     *
     * @param registerDTO validated registration payload (username, email, password)
     * @return 201 Created with JWT authorization token and user summary
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserRegisterDTO registerDTO) {
        AuthResponseDTO response = authService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates an existing user and issues a signed JWT token.
     *
     * Endpoint: POST /api/auth/login
     * Access: Public
     *
     * @param loginDTO validated login credentials (username/email, password)
     * @return 200 OK with JWT authorization token and user summary
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UserLoginDTO loginDTO) {
        AuthResponseDTO response = authService.login(loginDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves the profile details of the currently authenticated user.
     *
     * Endpoint: GET /api/auth/me
     * Access: Authenticated (Bearer JWT required)
     *
     * @param authentication Spring Security authentication principal injected by the container
     * @return 200 OK with the authenticated user's profile details (excluding password)
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {
        UserResponseDTO profile = authService.getCurrentUserProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }
}
