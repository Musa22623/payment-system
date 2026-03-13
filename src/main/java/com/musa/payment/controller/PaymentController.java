package com.musa.payment.controller;

import com.musa.payment.dto.PaymentRequest;
import com.musa.payment.dto.PaymentResponse;
import com.musa.payment.entity.PaymentStatus;
import com.musa.payment.entity.PaymentTransaction;
import com.musa.payment.event.PaymentEvent;
import com.musa.payment.kafka.PaymentProducer;
import com.musa.payment.repository.PaymentRepository;
import com.musa.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
//    private final PaymentRepository paymentRepository;
    private final PaymentProducer paymentProducer;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository,
                             PaymentProducer paymentProducer) {
        this.paymentService = paymentService;
//        this.paymentRepository = paymentRepository;
        this.paymentProducer = paymentProducer;
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request,
                                                         @RequestHeader("Idempotency-Key") String key) {

        PaymentResponse response = paymentService.createPayment(request, key);

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/payment/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request) {

        String idempotencyKey = request.getIdempotencyKey();

        // Generate PaymentEvent
        PaymentEvent event = new PaymentEvent(request.getTransactionId(), request.getAmount());

        // Send PaymentEvent to Kafka Producer
        paymentProducer.sendPayment(event, request.getIdempotencyKey());

        // Return Payment Status as 'PENDING'
        PaymentResponse response = new PaymentResponse(request.getTransactionId(),
                PaymentStatus.PENDING,
                "Payment request accepted",
                "/payment/status/" + request.getTransactionId(),
                LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/payment/status/{transactionId}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String transactionId) {
        PaymentTransaction paymentTransaction = paymentService.getPayment(transactionId);
        return ResponseEntity.ok(new PaymentResponse(transactionId,
                paymentTransaction.getStatus(),
                "Payment status" + paymentTransaction.getStatus(),
                "/payment/status/" + paymentTransaction.getTransactionId(),
                paymentTransaction.getUpdatedAt()));
    }
}
