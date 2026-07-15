package com.irctc.irctc_backend.modules.seat.repository;

import com.irctc.irctc_backend.modules.seat.entity.PassengerAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PassengerAllocationRepository extends JpaRepository<PassengerAllocation, Long> {

    List<PassengerAllocation> findByBookingId(Long bookingId);

    void deleteByBookingId(Long bookingId);

    @Query("""
        SELECT pa FROM PassengerAllocation pa
        JOIN pa.booking b
        WHERE b.train.id = :trainId
          AND b.travelDate = :travelDate
          AND pa.status <> 'CANCELLED'
    """)
    List<PassengerAllocation> findActiveAllocationsByTrainAndDate(
            @Param("trainId") Long trainId,
            @Param("travelDate") LocalDate travelDate
    );
}
