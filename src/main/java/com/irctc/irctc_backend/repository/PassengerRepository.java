package com.irctc.irctc_backend.repository;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    //  REQUIRED FOR CANCELLATION + VIEW DETAILS
    List<Passenger> findByBooking(Booking booking);

    // (optional but useful)
    void deleteByBooking(Booking booking);
}
