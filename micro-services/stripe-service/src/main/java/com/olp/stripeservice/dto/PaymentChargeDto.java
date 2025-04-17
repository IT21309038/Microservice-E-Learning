package com.olp.stripeservice.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PaymentChargeDto {
    private String userId;
    private String courseId;
    private Double amount;
    private Boolean success;
    private String message;
    private String cardNumber;
    private String cardExpiryMonth;
    private String cardExpiryYear;
    private Map<String,Object> additionalInfo = new HashMap<>();
}
