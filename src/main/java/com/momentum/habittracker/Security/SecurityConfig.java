package com.momentum.habittracker.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Main Spring Security configuration class.
 *
 * Purpose:
 * Configures the application's security posture:
 * 1. Enables stateless JWT-based session management (disabling HTTP sessions and CSRF).
 * 2. Defines public endpoints (/api/auth/**, /error) versus secured endpoints (e.g., /api/habits/**).
 * 3. Registers BCryptPasswordEncoder as the standard password hashing algorithm.
 * 4. Integrates DaoAuthenticationProvider with CustomUserDetailsService.
 * 5. Injects the custom JwtAuthenticationFilter into the security filter chain before standard authentication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Constructs SecurityConfig with all required security beans and handlers.
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Configures the password hashing algorithm.
     * Uses BCrypt with adaptive salting and work factor.
     *
     * @return a BCryptPasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the DAO authentication provider.
     * Links CustomUserDetailsService with the password encoder for credential validation.
     *
     * @return configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposes the AuthenticationManager bean from Spring Security configuration.
     * Used by AuthService to programmatically authenticate user credentials during login.
     *
     * @param config authentication configuration holder
     * @return AuthenticationManager instance
     * @throws Exception if manager cannot be obtained
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) for external clients like the React frontend.
     * Allows requests from local development ports (e.g., http://localhost:3000, http://localhost:5173).
     *
     * @return configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(
            "http://localhost:[*]",
            "http://127.0.0.1:[*]",
            "https://*.vercel.app",
            "https://vercel.app"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configures the HTTP security filter chain:
     * - Disables CSRF (safe for stateless REST APIs using JWT tokens).
     * - Enables CORS with default settings.
     * - Configures stateless session management (SessionCreationPolicy.STATELESS).
     * - Configures custom 401 error handler (JwtAuthenticationEntryPoint).
     * - Sets route authorization rules: public access to /api/auth/**, authenticated for all else.
     * - Inserts JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter.
     *
     * @param http the HttpSecurity builder
     * @return the built SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF because REST APIs do not use browser session cookies
                .csrf(AbstractHttpConfigurer::disable)
                // Enable default Cross-Origin Resource Sharing
                .cors(Customizer.withDefaults())
                // Stateless sessions — no server-side HTTP session will be created or used
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Custom 401 Unauthorized JSON error handler
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                // URL authorization mapping - permit static frontend assets and auth endpoints
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/static/**",
                                "/assets/**",
                                "/css/**",
                                "/js/**",
                                "/favicon.ico",
                                "/*.html",
                                "/*.css",
                                "/*.js",
                                "/api/auth/**",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // Attach DAO authentication provider
                .authenticationProvider(authenticationProvider())
                // Place custom JWT filter ahead of default username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
