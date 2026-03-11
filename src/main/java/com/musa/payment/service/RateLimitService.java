package com.musa.payment.service;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;

@Service
public class RateLimitService {

    private static final int MAX_REQUESTS = 5;  // Max Request Count
    private static final Duration REFRESH_INTERVAL = Duration.ofMinutes(1);  // Reset requests every minute

    private final Bucket bucket;

    public RateLimitService() {
        // Allow users to make 5 requests per minute
        this.bucket = Bucket.builder()
                .addLimit(io.github.bucket4j.Bandwidth.simple(MAX_REQUESTS, REFRESH_INTERVAL))
                .build();
    }

    public boolean isAllowed(HttpServletRequest request) {
        // Deduct tokens each time a request comes in and decide whether to allow the request or not.
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        return probe.isConsumed();
    }
}