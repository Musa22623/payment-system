package com.musa.payment.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisPaymentLockService {

    private final StringRedisTemplate redisTemplate;

    public RedisPaymentLockService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean lockPayment(String transactionId) {

        Boolean success = redisTemplate
                .opsForValue()
                .setIfAbsent("PAYMENT_LOCK:" + transactionId, "LOCKED",
                        Duration.ofMinutes(5));

        return Boolean.TRUE.equals(success);
    }
}