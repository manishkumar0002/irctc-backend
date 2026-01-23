package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Payment;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public String refund(Long bookingId) {

        // 1️ Get booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // 2️ Get payment (ID based – SAFE)
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // 3️ Validate states
        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Only confirmed bookings can be refunded");
        }

        if (!"SUCCESS".equals(payment.getPaymentStatus())) {
            throw new RuntimeException("Payment not successful");
        }

        // 4️ Refund logic (simulation)
        payment.setRefundId("REF-" + UUID.randomUUID().toString().substring(0, 10));
        payment.setRefundStatus("SUCCESS");
        payment.setRefundTimestamp(LocalDateTime.now());

        booking.setStatus("CANCELLED");

        // 5️ Save updates
        paymentRepository.save(payment);
        bookingRepository.save(booking);

        return "Refund successful";
    }
}
