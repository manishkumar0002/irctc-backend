package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Payment;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentWebhookService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    @Transactional
    public void processWebhook(String payload, String signature) {

        verifySignature(payload, signature);

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        JSONObject paymentEntity =
                json.getJSONObject("payload")
                        .getJSONObject("payment")
                        .getJSONObject("entity");

        String orderId = paymentEntity.getString("order_id");
        String paymentId = paymentEntity.getString("id");
        String status = paymentEntity.getString("status");
        String method = paymentEntity.getString("method");

        Long bookingId = extractBookingId(orderId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("payment.captured".equals(event)) {

            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setGatewayOrderId(orderId);
            payment.setGatewayPaymentId(paymentId);
            payment.setGatewaySignature(signature);
            payment.setAmount(booking.getSeatCount() * 500.0);
            payment.setPaymentMethod(method.toUpperCase());
            payment.setPaymentStatus("SUCCESS");
            payment.setPaymentTimestamp(LocalDateTime.now());

            paymentRepository.save(payment);

            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);
        }

        if ("payment.failed".equals(event)) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
        }
    }

    private void verifySignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(
                    webhookSecret.getBytes(), "HmacSHA256"));

            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            StringBuilder expected = new StringBuilder();
            for (byte b : hash) {
                expected.append(String.format("%02x", b));
            }

            if (!expected.toString().equals(signature)) {
                throw new RuntimeException("Invalid webhook signature");
            }
        } catch (Exception e) {
            throw new RuntimeException("Webhook verification failed");
        }
    }

    private Long extractBookingId(String orderId) {
        // orderId format: BOOKING_12 or BOOKING_45
        return Long.parseLong(orderId.split("_")[1]);
    }
}
