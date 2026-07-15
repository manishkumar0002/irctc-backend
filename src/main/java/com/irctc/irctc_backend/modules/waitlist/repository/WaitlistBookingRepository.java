package com.irctc.irctc_backend.modules.waitlist.repository;

import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.modules.waitlist.entity.WaitlistBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WaitlistBookingRepository extends JpaRepository<WaitlistBooking, Long> {

    List<WaitlistBooking> findByBookingId(Long bookingId);

    void deleteByBookingId(Long bookingId);

    @Query("""
        SELECT w FROM WaitlistBooking w
        JOIN w.booking b
        WHERE b.train.id = :trainId
          AND b.travelDate = :travelDate
          AND b.classType = :classType
          AND w.status = 'ACTIVE'
          AND w.queueType = :queueType
        ORDER BY w.queuePosition ASC
    """)
    List<WaitlistBooking> findActiveByTrainAndDateAndClass(
            @Param("trainId") Long trainId,
            @Param("travelDate") LocalDate travelDate,
            @Param("classType") ClassType classType,
            @Param("queueType") String queueType
    );

    @Query("""
        SELECT MAX(w.queuePosition) FROM WaitlistBooking w
        JOIN w.booking b
        WHERE b.train.id = :trainId
          AND b.travelDate = :travelDate
          AND b.classType = :classType
          AND w.queueType = :queueType
    """)
    Optional<Integer> findMaxQueuePosition(
            @Param("trainId") Long trainId,
            @Param("travelDate") LocalDate travelDate,
            @Param("classType") ClassType classType,
            @Param("queueType") String queueType
    );
}
