package com.musa.payment.service;

import com.musa.payment.entity.PaymentTransaction;
import com.musa.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final IdempotencyService idempotencyService;

    public PaymentService(PaymentRepository paymentRepository, IdempotencyService idempotencyService) {
        this.paymentRepository = paymentRepository;
        this.idempotencyService = idempotencyService;
    }

    public PaymentTransaction createPayment(String userId, Double amount) {

        PaymentTransaction tx = new PaymentTransaction();

        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setUserId(userId);
        tx.setAmount(amount);
        tx.setStatus("SUCCESS");
        tx.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(tx);
    }

    public PaymentTransaction processPayment(
            String transactionId,
            String userId,
            Double amount) {

        if (idempotencyService.isDuplicate(transactionId)) {
            throw new RuntimeException("Duplicate payment request");
        }

        PaymentTransaction tx = new PaymentTransaction();

        tx.setTransactionId(transactionId);
        tx.setUserId(userId);
        tx.setAmount(amount);
        tx.setStatus("SUCCESS");
        tx.setCreatedAt(LocalDateTime.now());

        return paymentRepository.save(tx);

        // Payment processing logic
    }
}

