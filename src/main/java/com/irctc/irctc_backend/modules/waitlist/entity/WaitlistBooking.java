package com.irctc.irctc_backend.modules.waitlist.entity;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Passenger;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "waitlist_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaitlistBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passenger_id", nullable = false)
    private Passenger passenger;

    @Column(name = "queue_type", nullable = false)
    private String queueType; // RAC, WL

    @Column(name = "queue_position", nullable = false)
    private Integer queuePosition; // e.g. 1, 2, 3...

    @Column(name = "status", nullable = false)
    private String status; // ACTIVE, PROMOTED, CANCELLED
}
