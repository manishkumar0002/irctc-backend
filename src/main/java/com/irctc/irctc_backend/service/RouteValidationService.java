package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.entity.Station;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.entity.TrainRoute;
import com.irctc.irctc_backend.entity.TrainStop;
import com.irctc.irctc_backend.repository.StationRepository;
import com.irctc.irctc_backend.repository.TrainRouteRepository;
import com.irctc.irctc_backend.repository.TrainStopRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RouteValidationService {

    private final TrainRouteRepository trainRouteRepository;
    private final TrainStopRepository trainStopRepository;
    private final StationRepository stationRepository;

    public void validateRoute(
            Train train,
            String sourceCode,
            String destinationCode
    ) {

        // 1️ Find route for train
        TrainRoute route = trainRouteRepository.findByTrain(train)
                .orElseThrow(() ->
                        new RuntimeException("Train route not found"));

        // 2️ Find stations
        Station source = stationRepository.findByCode(sourceCode)
                .orElseThrow(() ->
                        new RuntimeException("Source station not found"));

        Station destination = stationRepository.findByCode(destinationCode)
                .orElseThrow(() ->
                        new RuntimeException("Destination station not found"));

        // 3️ Find stops
        TrainStop sourceStop = trainStopRepository.findByRouteAndStation(route, source)
                .orElseThrow(() ->
                        new RuntimeException("Source station not in train route"));

        TrainStop destinationStop = trainStopRepository.findByRouteAndStation(route, destination)
                .orElseThrow(() ->
                        new RuntimeException("Destination station not in train route"));

        // 4️ Validate halts
        if (!sourceStop.isHalt() || !destinationStop.isHalt()) {
            throw new RuntimeException("Train does not halt at source or destination station");
        }

        // 5️ Validate order
        if (sourceStop.getStopOrder() >= destinationStop.getStopOrder()) {
            throw new RuntimeException("Invalid route: source station comes after destination station");
        }
    }
}
