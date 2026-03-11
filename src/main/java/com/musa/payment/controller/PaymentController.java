package com.musa.payment.controller;

import com.musa.payment.entity.PaymentTransaction;
import com.musa.payment.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentTransaction createPayment(
            @RequestParam String userId,
            @RequestParam Double amount) {

        return paymentService.createPayment(userId, amount);
    }
}
