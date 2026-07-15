package com.irctc.irctc_backend.modules.seat.repository;

import com.irctc.irctc_backend.modules.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByCoachId(Long coachId);
    void deleteByCoachId(Long coachId);
    List<Seat> findByCoachTrainId(Long trainId);
}
