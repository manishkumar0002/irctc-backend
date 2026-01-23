package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.dto.PaymentOrderResponse;
import com.irctc.irctc_backend.dto.PaymentVerifyRequest;
import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Payment;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
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
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;


    // CREATE PAYMENT ORDER (RACE-CONDITION SAFE)

    @Transactional

    public PaymentOrderResponse createOrder(Long bookingId) throws Exception {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!"PAYMENT_PENDING".equals(booking.getStatus())) {
            throw new RuntimeException("Invalid booking state");
        }

        // IDEMPOTENT PAYMENT INIT
        Payment existingPayment = paymentRepository.findByBookingId(bookingId).orElse(null);

        if (existingPayment != null && "CREATED".equals(existingPayment.getPaymentStatus())) {
            //  Return same order again
            return new PaymentOrderResponse(
                    existingPayment.getGatewayOrderId(),
                    existingPayment.getAmount(),
                    keyId
            );
        }

        //  Create new Razorpay order
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject options = new JSONObject();
        options.put("amount", booking.getSeatCount() * 500 * 100);
        options.put("currency", "INR");
        options.put("receipt", "BOOKING_" + bookingId);

        Order order = client.orders.create(options);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setGatewayOrderId(order.get("id"));
        payment.setAmount(booking.getSeatCount() * 500.0);
        payment.setPaymentMethod("RAZORPAY");
        payment.setPaymentStatus("CREATED");
        payment.setPaymentTimestamp(LocalDateTime.now());

        paymentRepository.save(payment);

        return new PaymentOrderResponse(
                order.get("id"),
                payment.getAmount(),
                keyId
        );
    }



    // VERIFY PAYMENT (IDEMPOTENT)

    @Transactional
    public void verifyPayment(PaymentVerifyRequest request) {

        Payment payment = paymentRepository
                .findByGatewayOrderId(request.razorpayOrderId())
                .orElseThrow(() -> new RuntimeException("Payment order not found"));

        if ("SUCCESS".equals(payment.getPaymentStatus())) {
            return; // idempotent
        }

        String payload =
                request.razorpayOrderId() + "|" + request.razorpayPaymentId();

        String generatedSignature = hmacSha256(payload, keySecret);

        if (!generatedSignature.equals(request.razorpaySignature())) {
            payment.setPaymentStatus("FAILED");
            paymentRepository.save(payment);
            throw new RuntimeException("Payment signature verification failed");
        }

        payment.setGatewayPaymentId(request.razorpayPaymentId());
        payment.setGatewaySignature(request.razorpaySignature());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setPaymentStatus("SUCCESS");

        Booking booking = payment.getBooking();
        booking.setStatus("CONFIRMED");

        paymentRepository.save(payment);
        bookingRepository.save(booking);
    }


    // SIGNATURE VALIDATION

    private String hmacSha256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Signature verification error", e);
        }
    }
}
