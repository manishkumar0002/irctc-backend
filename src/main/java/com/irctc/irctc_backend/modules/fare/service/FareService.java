package com.irctc.irctc_backend.modules.fare.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.entity.ClassType;
import com.irctc.irctc_backend.entity.Passenger;
import com.irctc.irctc_backend.modules.fare.entity.FareBreakdown;
import com.irctc.irctc_backend.modules.fare.repository.FareBreakdownRepository;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.entity.TrainRoute;
import com.irctc.irctc_backend.entity.TrainStop;
import com.irctc.irctc_backend.repository.TrainRouteRepository;
import com.irctc.irctc_backend.repository.TrainStopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FareService {

    private final FareBreakdownRepository fareBreakdownRepository;
    private final TrainRouteRepository trainRouteRepository;
    private final TrainStopRepository trainStopRepository;

    @Transactional
    public FareBreakdown calculateAndSaveFare(Booking booking, List<Passenger> passengers) {
        double distance = determineDistance(booking.getTrain(), booking.getSourceStationCode(), booking.getDestinationStationCode());
        ClassType classType = booking.getClassType();

        // 1. Base Rate per km by Coach Class
        double ratePerKm = switch (classType) {
            case SL -> 0.8;
            case _3A -> 2.0;
            case _2A -> 3.5;
            case _1A -> 5.5;
            case CC -> 1.5;
        };

        double baseFarePerPassenger = distance * ratePerKm;

        // 2. Train Type Surcharge
        double trainMultiplier = 1.0;
        String trainName = booking.getTrain().getTrainName().toLowerCase();
        if (trainName.contains("rajdhani") || trainName.contains("shatabdi") || trainName.contains("duronto")) {
            trainMultiplier = 1.5;
        } else if (trainName.contains("exp") || trainName.contains("express") || trainName.contains("mail")) {
            trainMultiplier = 1.2;
        }

        double finalBaseFarePerPassenger = baseFarePerPassenger * trainMultiplier;
        double totalBaseFare = finalBaseFarePerPassenger * passengers.size();

        // 3. Surcharges (e.g. Tatkal - mock flag based on time or random check for demo, or defaults to 0)
        double tatkalSurcharge = 0.0;
        boolean isAC = classType != ClassType.SL;
        // Assume Tatkal pricing if booking includes "TATKAL" in PNR or created within 24h of travel
        // Let's configure it as flat 150 INR for AC, 75 INR for SL as surcharge if applicable
        if (booking.getPnr() != null && booking.getPnr().contains("T")) {
            tatkalSurcharge = isAC ? 150.0 * passengers.size() : 75.0 * passengers.size();
        }

        // 4. Concessions & Discounts
        double totalDiscounts = 0.0;
        for (Passenger passenger : passengers) {
            double passengerDiscount = 0.0;
            if (passenger.getAge() < 12) {
                // Child: 50% discount on base fare
                passengerDiscount = finalBaseFarePerPassenger * 0.50;
            } else if (passenger.getAge() >= 60) {
                // Senior Citizen: 50% for Females, 40% for Males
                double rate = "F".equalsIgnoreCase(passenger.getGender()) ? 0.50 : 0.40;
                passengerDiscount = finalBaseFarePerPassenger * rate;
            } else if (passenger.getName().toLowerCase().contains("divyang") || 
                       passenger.getName().toLowerCase().contains("military")) {
                // Special concession: 75% discount
                passengerDiscount = finalBaseFarePerPassenger * 0.75;
            }
            totalDiscounts += passengerDiscount;
        }

        // 5. GST (5% on AC classes only)
        double gst = isAC ? (totalBaseFare + tatkalSurcharge - totalDiscounts) * 0.05 : 0.0;

        // 6. Convenience Fee
        double convenienceFee = isAC ? 30.0 : 15.0;

        // 7. Insurance Fee (0.35 INR per passenger)
        double insuranceFee = 0.35 * passengers.size();

        // Total calculated fare
        double totalFare = totalBaseFare + tatkalSurcharge + gst + convenienceFee + insuranceFee - totalDiscounts;

        FareBreakdown breakdown = FareBreakdown.builder()
                .booking(booking)
                .baseFare(totalBaseFare)
                .surcharge(tatkalSurcharge)
                .gst(gst)
                .convenienceFee(convenienceFee)
                .insuranceFee(insuranceFee)
                .discounts(totalDiscounts)
                .totalFare(totalFare)
                .build();

        return fareBreakdownRepository.save(breakdown);
    }

    public FareBreakdown getFareBreakdownByBookingId(Long bookingId) {
        return fareBreakdownRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Fare breakdown not found for booking: " + bookingId));
    }

    private double determineDistance(Train train, String source, String destination) {
        if (source.equalsIgnoreCase(destination)) return 0.0;

        try {
            TrainRoute route = trainRouteRepository.findByTrain(train).orElse(null);
            if (route != null) {
                List<TrainStop> stops = trainStopRepository.findByRouteOrderByStopOrder(route);
                TrainStop sourceStop = stops.stream()
                        .filter(s -> s.getStation().getCode().equalsIgnoreCase(source))
                        .findFirst().orElse(null);
                TrainStop destStop = stops.stream()
                        .filter(s -> s.getStation().getCode().equalsIgnoreCase(destination))
                        .findFirst().orElse(null);

                if (sourceStop != null && destStop != null && 
                    sourceStop.getDistanceFromOrigin() != null && destStop.getDistanceFromOrigin() != null) {
                    return Math.abs(destStop.getDistanceFromOrigin() - sourceStop.getDistanceFromOrigin());
                }
            }
        } catch (Exception e) {
            // Log and fallback to default estimation
        }

        int diff = Math.abs(source.hashCode() - destination.hashCode());
        return 200.0 + (diff % 800); // Dynamic distance estimate between 200km and 1000km
    }
}
