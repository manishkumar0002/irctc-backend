package com.irctc.irctc_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "train_stops",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"route_id", "stop_order"})
        }
)
public class TrainStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id")
    private TrainRoute route;

    @ManyToOne(optional = false)
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(name = "stop_order", nullable = false)
    private int stopOrder;

    @Column(nullable = false)
    private boolean halt;
}
