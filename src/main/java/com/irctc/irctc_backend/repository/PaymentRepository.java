package com.irctc.irctc_backend.repository;

import com.irctc.irctc_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Used while creating order (duplicate prevention)
    Optional<Payment> findByBookingId(Long bookingId);

    // Used while verifying payment
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);
}
