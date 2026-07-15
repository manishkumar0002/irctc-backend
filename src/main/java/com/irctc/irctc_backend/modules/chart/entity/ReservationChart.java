package com.irctc.irctc_backend.modules.chart.entity;

import com.irctc.irctc_backend.entity.Train;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "reservation_charts",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"train_id", "travel_date"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationChart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @Column(name = "chart_status", nullable = false)
    private String chartStatus; // PENDING, PREPARED, RELEASED

    @Column(name = "prepared_at")
    private LocalDateTime preparedAt;
}
