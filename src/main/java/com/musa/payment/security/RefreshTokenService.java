package com.musa.payment.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RefreshTokenService {

    // Simple storage to manage refresh tokens and expiration dates (in real services, they are stored in the DB)
    private Map<String, RefreshToken> refreshTokenStore = new HashMap<>();

    // Create and save a refresh token
    public RefreshToken generateRefreshToken(String username) {
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2592000000L)) // Valid for 30 days
                .signWith(SignatureAlgorithm.HS256, "payment-secret-key")
                .compact();

        RefreshToken token = new RefreshToken(refreshToken);
        refreshTokenStore.put(username, token);

        return token;
    }

    // Refresh Token Validation
    public boolean validateRefreshToken(String refreshToken) {
        // Verify token from saved refresh token
        return refreshTokenStore.values().stream()
                .anyMatch(token -> token.getRefreshToken().equals(refreshToken) &&
                        token.getCreatedAt().isAfter(LocalDateTime.now().minusDays(30))); // Tokens that are older than 30 days are invalid.
    }

    // Check the Refresh Token expiration time
    public boolean isRefreshTokenExpired(String refreshToken) {
        // Check Refresh Token expiration
        return refreshTokenStore.values().stream()
                .anyMatch(token -> token.getRefreshToken().equals(refreshToken) &&
                        token.getCreatedAt().isBefore(LocalDateTime.now().minusDays(30))); // If 30 days have passed
    }
}
