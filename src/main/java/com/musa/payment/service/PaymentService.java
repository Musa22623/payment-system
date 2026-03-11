package com.musa.payment.service;

import com.musa.payment.entity.PaymentTransaction;
import com.musa.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
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
}

