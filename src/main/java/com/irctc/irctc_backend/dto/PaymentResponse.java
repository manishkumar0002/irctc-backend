package com.irctc.irctc_backend.dto;

import java.time.LocalDateTime;

public record PaymentResponse (String transactionId,
                               double amount,
                               String paymentMethod,
                               String paymentStatus,
                               LocalDateTime paymentTimestamp
) {}