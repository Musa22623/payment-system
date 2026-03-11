package com.musa.payment.controller;

import com.musa.payment.dto.PaymentRequest;
import com.musa.payment.entity.PaymentTransaction;
import com.musa.payment.repository.PaymentRepository;
import com.musa.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

//    @PostMapping
//    public PaymentTransaction createPayment(
//            @RequestParam String transactionId,
//            @RequestParam String userId,
//            @RequestParam Double amount) {
//
//        return paymentService.processPayment(transactionId, userId, amount);
//    }

    @PostMapping("/process")
    public PaymentTransaction pay(@RequestBody PaymentRequest request) {

        return paymentService.processPayment(
                request.getTransactionId(),
                request.getAmount()
        );
    }

    @GetMapping("/{transactionId}")
    public PaymentTransaction getPayment(@PathVariable String transactionId) {
        return paymentService.getPayment(transactionId);
    }
}
