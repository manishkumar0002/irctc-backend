package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.service.PaymentWebhookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentWebhookService webhookService;

    @PostMapping
    public String handleWebhook(HttpServletRequest request,
                                @RequestHeader("X-Razorpay-Signature") String signature)
            throws Exception {

        String payload = request.getReader()
                .lines()
                .collect(Collectors.joining("\n"));

        webhookService.processWebhook(payload, signature);

        return "OK";
    }
}
