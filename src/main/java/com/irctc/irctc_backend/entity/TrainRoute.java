package com.irctc.irctc_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "train_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    // OPTIONAL but very useful
    public TrainRoute(Train train) {
        this.train = train;
    }
}
