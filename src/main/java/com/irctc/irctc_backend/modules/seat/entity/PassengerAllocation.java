package com.irctc.irctc_backend.modules.seat.entity;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Passenger;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passenger_allocations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat; // Nullable for RAC/Waitlist

    @Column(name = "coach_number")
    private String coachNumber;

    @Column(name = "seat_number")
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "berth_type")
    private BerthType berthType;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, CONFIRMED, RAC, WAITLIST, CANCELLED
}
