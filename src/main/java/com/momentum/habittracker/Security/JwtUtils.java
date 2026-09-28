package com.momentum.habittracker.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility component for generating, signing, and validating JSON Web Tokens (JWT).
 *
 * Purpose:
 * Centralizes all cryptographic and parsing logic related to JWT security tokens.
 * Generates HMAC-SHA256 signed access tokens upon user login/registration, extracts
 * claims (subject, expiration date), and cryptographically validates tokens passed in
 * HTTP Authorization headers.
 */
@Component
public class JwtUtils {

    /** Secret encryption key injected from application.properties */
    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String jwtSecret;

    /** Token validity duration in milliseconds (default: 7 days) */
    @Value("${jwt.expiration-ms:604800000}")
    private long jwtExpirationMs;

    /**
     * Derives a cryptographic {@link SecretKey} for HMAC-SHA signing.
     * Decodes Base64 if possible; falls back to raw UTF-8 bytes if needed.
     *
     * @return the HMAC signing key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
            if (keyBytes.length < 32) {
                keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a signed JWT access token for the specified username.
     *
     * @param username the subject username to embed in the token
     * @return compact signed JWT string
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extracts the subject (username) claim from the token.
     *
     * @param token the JWT token string
     * @return the extracted username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date claim from the token.
     *
     * @param token the JWT token string
     * @return the token expiration timestamp
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Generic helper method to resolve a specific claim from the token claims payload.
     *
     * @param token the JWT token string
     * @param claimsResolver functional mapper to extract the desired claim
     * @param <T> type of the extracted claim
     * @return the extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the signed JWT token and extracts all embedded claims after verifying signature.
     *
     * @param token the JWT token string
     * @return the Claims payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks whether the token has passed its expiration time.
     *
     * @param token the JWT token string
     * @return true if expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates that the token's subject matches the given UserDetails and that the token is not expired.
     *
     * @param token the JWT token string
     * @param userDetails user details to match against the token subject
     * @return true if signature is valid, subject matches, and token is active; false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
