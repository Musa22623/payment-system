package com.musa.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    private String idempotencyKey;
    private String transactionId;
    private Long amount;

}
