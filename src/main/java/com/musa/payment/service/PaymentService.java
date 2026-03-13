package com.musa.payment.service;

import com.musa.payment.dto.PaymentRequest;
import com.musa.payment.dto.PaymentResponse;
import com.musa.payment.entity.Payment;
import com.musa.payment.entity.PaymentStatus;
import com.musa.payment.entity.PaymentTransaction;
import com.musa.payment.event.PaymentEvent;
import com.musa.payment.kafka.PaymentProducer;
import com.musa.payment.redis.RedisPaymentLockService;
import com.musa.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final IdempotencyService idempotencyService;
    private final PaymentProducer paymentProducer;

    public PaymentService(PaymentRepository paymentRepository,
                          IdempotencyService idempotencyService,
                          PaymentProducer paymentProducer) {
        this.paymentRepository = paymentRepository;
        this.idempotencyService = idempotencyService;
        this.paymentProducer = paymentProducer;
    }

    public PaymentResponse createPayment(PaymentRequest request, String key) {

        if (!idempotencyService.tryProcess(key)) {
            throw new RuntimeException("Duplicate request");
        }

        String transactionId = UUID.randomUUID().toString();

        PaymentTransaction payment = new PaymentTransaction();

        payment.setTransactionId(transactionId);
        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        paymentProducer.sendPayment(new PaymentEvent(transactionId, request.getAmount()), key);

        return PaymentResponse.builder()
                .transactionId(transactionId)
                .status(PaymentStatus.PENDING)
                .statusUrl("/api/payments/" + transactionId)
                .message("Message PENDING")
                .build();
    }

    @Transactional
    public PaymentTransaction processPayment(String transactionId, Long amount) {

        PaymentTransaction payment = new PaymentTransaction();

        payment.setTransactionId(transactionId);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.REQUESTED);
        payment.setCreatedAt(LocalDateTime.now());

//        payment = paymentRepository.save(payment);

        // Start payment processing
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        try {

            // Processing
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

