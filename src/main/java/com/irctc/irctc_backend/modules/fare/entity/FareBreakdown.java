package com.irctc.irctc_backend.modules.fare.entity;

import com.irctc.irctc_backend.entity.Booking;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fare_breakdowns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareBreakdown {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "base_fare", nullable = false)
    private Double baseFare;

    @Column(name = "surcharge", nullable = false)
    private Double surcharge; // e.g. Tatkal / Premium Tatkal / Festival surcharges

    @Column(name = "gst", nullable = false)
    private Double gst;

    @Column(name = "convenience_fee", nullable = false)
    private Double convenienceFee;

    @Column(name = "insurance_fee", nullable = false)
    private Double insuranceFee;

    @Column(name = "discounts", nullable = false)
    private Double discounts;

    @Column(name = "total_fare", nullable = false)
    private Double totalFare;
}
