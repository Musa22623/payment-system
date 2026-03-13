package com.musa.payment.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isDuplicate(String key) {

        Boolean exists = redisTemplate.hasKey("IDEMPOTENCY:" + key);

        return Boolean.TRUE.equals(exists);
    }

    public void saveKey(String key) {

        redisTemplate.opsForValue()
                .set("IDEMPOTENCY:" + key, "PROCESSED", Duration.ofMinutes(10));
    }

    public boolean tryProcess(String key) {

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent("IDEMPOTENCY:" + key, "PROCESSING", Duration.ofMinutes(10));

        return Boolean.TRUE.equals(success);
    }
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    public IdempotencyService(RedisTemplate<String, Object> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }
//
//    public boolean isDuplicate(String transactionId) {
//
//        Boolean exists = redisTemplate.hasKey(transactionId);
//
//        if (Boolean.TRUE.equals(exists)) {
//            return true;
//        }
//
//        redisTemplate.opsForValue().set(transactionId, "processed");
//
//        return false;
//    }
}
