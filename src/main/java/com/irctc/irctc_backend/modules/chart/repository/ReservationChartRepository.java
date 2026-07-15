package com.irctc.irctc_backend.modules.chart.repository;

import com.irctc.irctc_backend.modules.chart.entity.ReservationChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservationChartRepository extends JpaRepository<ReservationChart, Long> {
    Optional<ReservationChart> findByTrainIdAndTravelDate(Long trainId, LocalDate travelDate);
}
