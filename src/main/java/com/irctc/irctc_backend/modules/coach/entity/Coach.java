package com.irctc.irctc_backend.modules.coach.entity;

import com.irctc.irctc_backend.entity.Train;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coaches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "coach_number", nullable = false)
    private String coachNumber; // e.g. "S1", "B1"

    @Column(name = "coach_name", nullable = false)
    private String coachName; // e.g. "Sleeper Coach 1"

    @Enumerated(EnumType.STRING)
    @Column(name = "coach_type", nullable = false)
    private CoachType coachType;

    @Column(name = "seat_capacity", nullable = false)
    private Integer seatCapacity;

    @Column(name = "coach_position")
    private Integer coachPosition; // Position in train composition

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CoachStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id")
    private Train train;
}
