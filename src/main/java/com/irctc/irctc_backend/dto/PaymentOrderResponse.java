package com.irctc.irctc_backend.dto;

public record PaymentOrderResponse(
        String orderId,
        Double amount,
        String razorpayKey
) {}
