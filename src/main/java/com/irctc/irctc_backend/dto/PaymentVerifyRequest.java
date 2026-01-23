package com.irctc.irctc_backend.dto;

public record PaymentVerifyRequest(
        Long bookingId,
        String razorpayOrderId,
        String razorpayPaymentId,
        String razorpaySignature,
        String paymentMethod
) {}
