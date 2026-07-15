package com.irctc.irctc_backend.modules.pnr.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.Passenger;
import com.irctc.irctc_backend.modules.pnr.dto.PassengerPnrStatus;
import com.irctc.irctc_backend.modules.pnr.dto.PnrStatusResponse;
import com.irctc.irctc_backend.modules.seat.entity.PassengerAllocation;
import com.irctc.irctc_backend.modules.seat.repository.PassengerAllocationRepository;
import com.irctc.irctc_backend.modules.waitlist.entity.WaitlistBooking;
import com.irctc.irctc_backend.modules.waitlist.repository.WaitlistBookingRepository;
import com.irctc.irctc_backend.repository.BookingRepository;
import com.irctc.irctc_backend.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PnrService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final PassengerAllocationRepository passengerAllocationRepository;
    private final WaitlistBookingRepository waitlistBookingRepository;
    private final com.irctc.irctc_backend.modules.chart.repository.ReservationChartRepository reservationChartRepository;

    public PnrStatusResponse getPnrStatus(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr)
                .orElseThrow(() -> new RuntimeException("PNR not found: " + pnr));

        List<Passenger> passengers = passengerRepository.findByBooking(booking);

        List<PassengerAllocation> allocations = passengerAllocationRepository.findByBookingId(booking.getId());
        Map<Long, PassengerAllocation> allocationMap = allocations.stream()
                .collect(Collectors.toMap(pa -> pa.getPassenger().getId(), pa -> pa, (pa1, pa2) -> pa1));

        List<WaitlistBooking> waitlistBookings = waitlistBookingRepository.findByBookingId(booking.getId());
        Map<Long, WaitlistBooking> wlMap = waitlistBookings.stream()
                .collect(Collectors.toMap(w -> w.getPassenger().getId(), w -> w, (w1, w2) -> w1));

        List<PassengerPnrStatus> passengerStatuses = new ArrayList<>();
        for (Passenger passenger : passengers) {
            PassengerAllocation allocation = allocationMap.get(passenger.getId());
            WaitlistBooking wl = wlMap.get(passenger.getId());

            String bookingStatusVal = "CNF";
            String currentStatusVal = "CNF";

            if (allocation != null) {
                if ("RAC".equals(allocation.getStatus())) {
                    bookingStatusVal = "RAC";
                    currentStatusVal = wl != null ? "RAC/" + wl.getQueuePosition() : "RAC";
                } else if ("WAITLIST".equals(allocation.getStatus()) || "WL".equals(allocation.getStatus())) {
                    bookingStatusVal = "WL";
                    currentStatusVal = wl != null ? "WL/" + wl.getQueuePosition() : "WL";
                }
            }

            PassengerPnrStatus passengerStatus = PassengerPnrStatus.builder()
                    .name(passenger.getName())
                    .age(passenger.getAge())
                    .gender(passenger.getGender())
                    .coachNumber(allocation != null ? allocation.getCoachNumber() : null)
                    .seatNumber(allocation != null ? allocation.getSeatNumber() : null)
                    .berthType(allocation != null && allocation.getBerthType() != null ? allocation.getBerthType().name() : null)
                    .bookingStatus(bookingStatusVal)
                    .currentStatus(currentStatusVal)
                    .build();

            passengerStatuses.add(passengerStatus);
        }

        String chartStatusVal = reservationChartRepository.findByTrainIdAndTravelDate(booking.getTrain().getId(), booking.getTravelDate())
                .map(com.irctc.irctc_backend.modules.chart.entity.ReservationChart::getChartStatus)
                .orElse("PENDING");

        return PnrStatusResponse.builder()
                .pnr(booking.getPnr())
                .trainNumber(booking.getTrain().getTrainNumber())
                .trainName(booking.getTrain().getTrainName())
                .travelDate(booking.getTravelDate())
                .classType(booking.getClassType().name())
                .fromStationCode(booking.getSourceStationCode())
                .toStationCode(booking.getDestinationStationCode())
                .bookingStatus(booking.getStatus())
                .chartStatus(chartStatusVal)
                .passengers(passengerStatuses)
                .build();
    }
}
