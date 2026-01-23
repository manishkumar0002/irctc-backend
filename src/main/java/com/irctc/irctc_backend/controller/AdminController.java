package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.entity.*;
import com.irctc.irctc_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/routes")
public class AdminController {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final TrainRouteRepository trainRouteRepository;
    private final TrainStopRepository trainStopRepository;

    // ADD STOP
    @PostMapping("/{trainId}/stops")
    @Transactional
    public TrainStop addStop(
            @PathVariable Long trainId,
            @RequestParam String stationCode,
            @RequestParam boolean halt
    ) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        TrainRoute route = trainRouteRepository.findByTrain(train)
                .orElseGet(() -> trainRouteRepository.save(new TrainRoute(train)));

        Station station = stationRepository.findByCode(stationCode)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        // prevent duplicate station in route
        trainStopRepository.findByRouteAndStation(route, station)
                .ifPresent(s -> {
                    throw new RuntimeException("Station already exists in route");
                });

        // AUTO stopOrder
        int nextStopOrder =
                trainStopRepository
                        .findTopByRouteOrderByStopOrderDesc(route)
                        .map(s -> s.getStopOrder() + 1)
                        .orElse(1);

        TrainStop stop = new TrainStop();
        stop.setRoute(route);
        stop.setStation(station);
        stop.setStopOrder(nextStopOrder);
        stop.setHalt(halt);

        return trainStopRepository.save(stop);
    }

    public TrainStop addStop(
            @PathVariable Long trainId,
            @RequestParam String stationCode,
            @RequestParam int stopOrder,
            @RequestParam boolean halt
    ) {

        if (stopOrder < 1) {
            throw new RuntimeException("Stop order must be >= 1");
        }

        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        TrainRoute route = trainRouteRepository.findByTrain(train)
                .orElseGet(() -> trainRouteRepository.save(new TrainRoute(train)));

        Station station = stationRepository.findByCode(stationCode)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        // prevent duplicate station
        trainStopRepository.findByRouteAndStation(route, station)
                .ifPresent(s -> {
                    throw new RuntimeException("Station already added in route");
                });

        // prevent duplicate order
        trainStopRepository.findByRouteAndStopOrder(route, stopOrder)
                .ifPresent(s -> {
                    throw new RuntimeException("Stop order already used");
                });

        TrainStop stop = new TrainStop();
        stop.setRoute(route);
        stop.setStation(station);
        stop.setStopOrder(stopOrder);
        stop.setHalt(halt);

        return trainStopRepository.save(stop);
    }

    // VIEW FULL ROUTE
    @GetMapping("/{trainId}")
    public List<TrainStop> getRoute(@PathVariable Long trainId) {

        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        TrainRoute route = trainRouteRepository.findByTrain(train)
                .orElseThrow(() -> new RuntimeException("Route not defined"));

        return trainStopRepository.findByRouteOrderByStopOrder(route);
    }

    // DELETE STOP
    @DeleteMapping("/stops/{stopId}")
    public String deleteStop(@PathVariable Long stopId) {
        trainStopRepository.deleteById(stopId);
        return "Stop removed successfully";
    }
}