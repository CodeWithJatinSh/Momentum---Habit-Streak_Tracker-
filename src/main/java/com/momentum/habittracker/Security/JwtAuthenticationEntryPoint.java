package com.momentum.habittracker.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles unauthorized access attempts by unauthenticated users.
 *
 * Purpose:
 * Implements Spring Security's {@link AuthenticationEntryPoint}.
 * Invoked whenever an unauthenticated client attempts to access a protected endpoint
 * without providing a valid JWT Bearer token. Formats and writes a structured 401 Unauthorized
 * JSON response payload directly to the HTTP response stream rather than redirecting
 * or rendering an HTML error page.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /** Jackson JSON object mapper used to serialize the error map */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Renders a 401 Unauthorized JSON error response when authentication fails.
     *
     * @param request the HTTP request being serviced
     * @param response the HTTP response stream to write to
     * @param authException the security exception indicating the reason for refusal
     * @throws IOException in the event of an I/O error writing the response body
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage() != null
                ? authException.getMessage()
                : "Full authentication is required to access this resource");
        body.put("path", request.getServletPath());
        body.put("timestamp", LocalDateTime.now().toString());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
