package com.musa.payment.security;

import java.time.LocalDateTime;

public class RefreshToken {

    private String refreshToken;
    private LocalDateTime createdAt;

    public RefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        this.createdAt = LocalDateTime.now();
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
