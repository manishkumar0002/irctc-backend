package com.irctc.irctc_backend.repository;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    Optional<Booking> findByPnr(String pnr);

    List<Booking> findByStatusAndCreatedAtBefore(
            String status,
            LocalDateTime time
    );

    //  HARD DB LOCK (KEY FIX)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdForUpdate(Long id);
}
