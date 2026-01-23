package com.irctc.irctc_backend.repository;

import com.irctc.irctc_backend.entity.Station;
import com.irctc.irctc_backend.entity.TrainRoute;
import com.irctc.irctc_backend.entity.TrainStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainStopRepository extends JpaRepository<TrainStop, Long> {

    // Get full route in order
    List<TrainStop> findByRouteOrderByStopOrder(TrainRoute route);

    // Check if station already exists in route
    Optional<TrainStop> findByRouteAndStation(TrainRoute route, Station station);

    // Check duplicate stop order
    Optional<TrainStop> findByRouteAndStopOrder(TrainRoute route, int stopOrder);
    Optional<TrainStop> findTopByRouteOrderByStopOrderDesc(TrainRoute route);

    // Search trains stopping at a station (IRCTC-style)
    @Query("""
        SELECT ts FROM TrainStop ts
        JOIN FETCH ts.route r
        JOIN FETCH r.train t
        WHERE ts.station.code = :stationCode
          AND ts.halt = true
    """)
    List<TrainStop> findStoppingTrainsAtStation(
            @Param("stationCode") String stationCode
    );
}
