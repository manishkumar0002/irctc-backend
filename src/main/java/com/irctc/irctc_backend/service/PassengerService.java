package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.dto.PassengerRequest;
import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Passenger;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PassengerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor

public class PassengerService {

    private  final PassengerRepository passengerRepository;
    private final BookingRepository bookingRepository;


    @Transactional
    public List<Passenger> addPassengers(
            Long bookingId,
            List<PassengerRequest> requests
    ){
        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(()->new RuntimeException("Booking not found with id: "+bookingId));
        return requests.stream().map(request->{
            Passenger passenger=new Passenger();
            passenger.setBooking(booking);
            passenger.setName(request.getName());
            passenger.setAge(request.getAge());
            passenger.setGender(request.getGender());
            return passengerRepository.save(passenger);

        }).toList();
    }
}
