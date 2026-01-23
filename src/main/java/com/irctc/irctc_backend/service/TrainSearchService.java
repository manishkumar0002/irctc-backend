package com.irctc.irctc_backend.service;

import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.entity.TrainStop;
import com.irctc.irctc_backend.repository.TrainStopRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrainSearchService {

    private final TrainStopRepository trainStopRepository;

    public List<Train> searchTrains(
            String sourceCode,
            String destinationCode
    ) {

        // 1️ Trains stopping at source
        List<TrainStop> sourceStops =
                trainStopRepository.findStoppingTrainsAtStation(sourceCode);

        // 2️ Trains stopping at destination
        List<TrainStop> destinationStops =
                trainStopRepository.findStoppingTrainsAtStation(destinationCode);

        // 3️ Map: trainId → source TrainStop
        Map<Long, TrainStop> sourceMap = sourceStops.stream()
                .collect(Collectors.toMap(
                        ts -> ts.getRoute().getTrain().getId(),
                        ts -> ts
                ));

        List<Train> result = new ArrayList<>();

        // 4️ Validate order
        for (TrainStop destStop : destinationStops) {

            Long trainId = destStop.getRoute().getTrain().getId();

            // check same train exists at source
            if (sourceMap.containsKey(trainId)) {

                TrainStop sourceStop = sourceMap.get(trainId);

                // source must come before destination
                if (sourceStop.getStopOrder() < destStop.getStopOrder()) {
                    result.add(destStop.getRoute().getTrain());
                }
            }
        }

        return result;
    }
}
