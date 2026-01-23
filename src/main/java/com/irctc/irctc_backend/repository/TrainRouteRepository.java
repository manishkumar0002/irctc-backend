package com.irctc.irctc_backend.repository;

import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.entity.TrainRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainRouteRepository extends JpaRepository<TrainRoute, Long> {

    Optional<TrainRoute> findByTrain(Train train);
}
