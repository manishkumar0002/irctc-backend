package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.service.RefundService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refunds")
@AllArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping("/{bookingId}")
    public String refund(@PathVariable Long bookingId) {
        return refundService.refund(bookingId);
    }
}
