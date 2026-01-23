package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.dto.PaymentOrderResponse;
import com.irctc.irctc_backend.dto.PaymentVerifyRequest;
import com.irctc.irctc_backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Create Razorpay order
    @PostMapping("/create-order/{bookingId}")
    public PaymentOrderResponse createOrder(@PathVariable Long bookingId) throws Exception {
        return paymentService.createOrder(bookingId);
    }

    // Verify payment
    @PostMapping("/verify")
    public String verifyPayment(@RequestBody PaymentVerifyRequest request) {
        paymentService.verifyPayment(request);
        return "Payment verified & booking confirmed";
    }
}
