package com.irctc.irctc_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Train train;

    private LocalDate travelDate;

    @Enumerated(EnumType.STRING)
    private ClassType classType;

    private int seatCount;

    private String status; // PAYMENT_PENDING / CONFIRMED / CANCELLED

    @Column(unique = true)
    private String pnr;

    private String sourceStationCode;
    private String destinationStationCode;

    // ✅ REQUIRED
    private LocalDateTime createdAt;
}
