package com.musa.payment.service;

import com.musa.payment.entity.Payment;
import com.musa.payment.entity.PaymentStatus;
import com.musa.payment.entity.PaymentTransaction;
import com.musa.payment.redis.RedisPaymentLockService;
import com.musa.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RedisPaymentLockService redisLock;

    public PaymentService(PaymentRepository paymentRepository,
                          RedisPaymentLockService redisLock) {
        this.paymentRepository = paymentRepository;
        this.redisLock = redisLock;
    }

//    public PaymentTransaction createPayment(String userId, Double amount) {
//
//        PaymentTransaction tx = new PaymentTransaction();
//
//        tx.setTransactionId(UUID.randomUUID().toString());
////        tx.setUserId(userId);
//        tx.setAmount(amount);
//        tx.setStatus("SUCCESS");
//        tx.setCreatedAt(LocalDateTime.now());
//
//        return paymentRepository.save(tx);
//    }

    @Transactional
    public PaymentTransaction processPayment(String transactionId, Long amount) {

        boolean locked = redisLock.lockPayment(transactionId);

        if (!locked) {
            throw new RuntimeException("Duplicate payment request");
        }

        PaymentTransaction payment = new PaymentTransaction();

        payment.setTransactionId(transactionId);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.REQUESTED);
        payment.setCreatedAt(LocalDateTime.now());

        payment = paymentRepository.save(payment);

        // Start payment processing
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        try {

            // 실제 결제 처리 (예시)
            Thread.sleep(500);

            payment.setStatus(PaymentStatus.SUCCESS);

        } catch (Exception e) {

            payment.setStatus(PaymentStatus.FAILED);
        }

        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public PaymentTransaction getPayment(String transactionId) {

        return paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}

