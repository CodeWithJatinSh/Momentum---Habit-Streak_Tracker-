package com.momentum.habittracker.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that intercepts incoming HTTP requests to validate JWT Bearer tokens.
 *
 * Purpose:
 * Executes once per request. Inspects the HTTP 'Authorization' header for a 'Bearer <token>'.
 * If a valid token is found, extracts the username, verifies the token's signature and expiration,
 * loads the user's granted authorities via {@link CustomUserDetailsService}, and populates
 * Spring Security's {@link SecurityContextHolder}. Downstream filters, controllers, and services
 * can then access the authenticated user.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** Utility for token parsing, extraction, and cryptographic validation */
    private final JwtUtils jwtUtils;

    /** Service to look up user details and granted authorities */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructs the filter with required dependencies.
     *
     * @param jwtUtils token utility component
     * @param userDetailsService user lookup service
     */
    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Internal filter logic executed for each incoming HTTP request.
     *
     * @param request incoming HTTP servlet request
     * @param response outgoing HTTP servlet response
     * @param filterChain the remaining filter chain to delegate to
     * @throws ServletException in case of servlet errors
     * @throws IOException in case of I/O errors
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Read the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");

        // Step 2: Check if header contains a Bearer token; if not, pass to next filter
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the raw JWT token string (skip "Bearer " prefix)
        final String jwt = authHeader.substring(7);
        final String username;

        try {
            username = jwtUtils.extractUsername(jwt);
        } catch (Exception e) {
            // Malformed, invalid, or expired token — proceed without authentication
            filterChain.doFilter(request, response);
            return;
        }

        // Step 4: If username is found and user is not yet authenticated in this thread context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Step 5: Validate token validity against the loaded UserDetails
            if (jwtUtils.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 6: Set authenticated user in the Spring Security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Step 7: Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
