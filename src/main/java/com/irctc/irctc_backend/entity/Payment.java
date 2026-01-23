package com.irctc.irctc_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "gateway_order_id", nullable = false)
    private String gatewayOrderId;


    @Column(name = "gateway_payment_id", nullable = true)
    private String gatewayPaymentId;

    @Column(name = "gateway_signature", nullable = true)
    private String gatewaySignature;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String paymentStatus;

    @Column(nullable = false)
    private LocalDateTime paymentTimestamp;

    // Refund (nullable)
    private String refundId;
    private String refundStatus;
    private LocalDateTime refundTimestamp;
}
