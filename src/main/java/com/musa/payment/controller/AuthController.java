package com.musa.payment.controller;

import com.musa.payment.dto.LoginRequest;
import com.musa.payment.security.JwtUtil;
import com.musa.payment.security.RefreshToken;
import com.musa.payment.security.RefreshTokenService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public AuthController(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    // Returns Access Token and Refresh Token upon login
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        if ("admin".equals(loginRequest.getUsername()) && "password".equals(loginRequest.getPassword())) {
            // Access Token 생성
            String accessToken = jwtUtil.generateAccessToken(loginRequest.getUsername());
            // Refresh Token 생성
            RefreshToken refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());

            return "Access Token: " + accessToken + "\nRefresh Token: " + refreshToken.getRefreshToken();
        }
        throw new RuntimeException("Invalid login credentials");
    }

    // Issue a new Access Token using a Refresh Token
    @PostMapping("/refresh-token")
    public String refreshToken(@RequestParam String refreshToken) {
        // Refresh Token Validation
        if (refreshTokenService.isRefreshTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh Token expired");
        }

        String username = jwtUtil.extractUsername(refreshToken);

        // If Refresh Token is valid, issue a new Access Token
        String newAccessToken = jwtUtil.generateAccessToken(username);
        return "New Access Token: " + newAccessToken;
    }
}