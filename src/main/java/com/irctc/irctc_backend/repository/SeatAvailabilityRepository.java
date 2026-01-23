package com.irctc.irctc_backend.repository;

import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.entity.SeatAvailability;
import com.irctc.irctc_backend.entity.Train;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;

public interface SeatAvailabilityRepository
        extends JpaRepository<SeatAvailability, Long> {

    Optional<SeatAvailability> findByTrainAndTravelDateAndClassType(
            Train train,
            LocalDate travelDate,
            ClassType classType
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT sa FROM SeatAvailability sa
        WHERE sa.train = :train
          AND sa.travelDate = :travelDate
          AND sa.classType = :classType
    """)
    Optional<SeatAvailability> lockSeatForUpdate(
            @Param("train") Train train,
            @Param("travelDate") LocalDate travelDate,
            @Param("classType") ClassType classType
    );
}
