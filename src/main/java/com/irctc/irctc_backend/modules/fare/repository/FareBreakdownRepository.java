package com.irctc.irctc_backend.modules.fare.repository;

import com.irctc.irctc_backend.modules.fare.entity.FareBreakdown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FareBreakdownRepository extends JpaRepository<FareBreakdown, Long> {
    Optional<FareBreakdown> findByBookingId(Long bookingId);
    void deleteByBookingId(Long bookingId);
}
