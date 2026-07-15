package com.irctc.irctc_backend.modules.tracking.service;

import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.entity.TrainRoute;
import com.irctc.irctc_backend.entity.TrainStop;
import com.irctc.irctc_backend.modules.tracking.dto.TrainLiveLocation;
import com.irctc.irctc_backend.repository.TrainRepository;
import com.irctc.irctc_backend.repository.TrainRouteRepository;
import com.irctc.irctc_backend.repository.TrainStopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrainRepository trainRepository;
    private final TrainRouteRepository trainRouteRepository;
    private final TrainStopRepository trainStopRepository;

    private static final Map<String, double[]> COORD_MAP = new HashMap<>();

    static {
        COORD_MAP.put("NDLS", new double[]{28.642, 77.219});
        COORD_MAP.put("HWH", new double[]{22.583, 88.342});
        COORD_MAP.put("BCT", new double[]{18.969, 72.815});
        COORD_MAP.put("SBC", new double[]{12.978, 77.572});
        COORD_MAP.put("MAS", new double[]{13.082, 80.275});
        COORD_MAP.put("GKP", new double[]{26.760, 83.362});
    }

    public TrainLiveLocation getLiveLocation(Long trainId) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found: " + trainId));

        TrainRoute route = trainRouteRepository.findByTrain(train).orElse(null);
        if (route == null) {
            return getDefaultLocation(train, "NOT_STARTED", "N/A", "N/A", 28.642, 77.219);
        }

        List<TrainStop> stops = trainStopRepository.findByRouteOrderByStopOrder(route);
        if (stops.isEmpty()) {
            return getDefaultLocation(train, "NOT_STARTED", "N/A", "N/A", 28.642, 77.219);
        }

        LocalTime now = LocalTime.now();

        // 1. If time is before first departure
        TrainStop first = stops.get(0);
        LocalTime firstDep = parseTime(first.getDepartureTime(), LocalTime.of(8, 0));
        if (now.isBefore(firstDep)) {
            double[] coords = getCoordinates(first.getStation().getCode());
            return getDefaultLocation(train, "NOT_STARTED", first.getStation().getCode(), stops.size() > 1 ? stops.get(1).getStation().getCode() : "N/A", coords[0], coords[1]);
        }

        // 2. If time is after last arrival
        TrainStop last = stops.get(stops.size() - 1);
        LocalTime lastArr = parseTime(last.getArrivalTime(), LocalTime.of(22, 0));
        if (now.isAfter(lastArr)) {
            double[] coords = getCoordinates(last.getStation().getCode());
            return getDefaultLocation(train, "ARRIVED", last.getStation().getCode(), "N/A", coords[0], coords[1]);
        }

        // 3. Find current running interval
        TrainStop currentStop = first;
        TrainStop nextStop = last;

        for (int i = 0; i < stops.size() - 1; i++) {
            TrainStop s1 = stops.get(i);
            TrainStop s2 = stops.get(i + 1);

            LocalTime t1 = parseTime(s1.getDepartureTime(), LocalTime.of(8 + i, 0));
            LocalTime t2 = parseTime(s2.getArrivalTime(), LocalTime.of(9 + i, 0));

            if (now.isAfter(t1) && now.isBefore(t2)) {
                currentStop = s1;
                nextStop = s2;
                break;
            }
        }

        // Calculate interpolation fraction between currentStop and nextStop
        LocalTime t1 = parseTime(currentStop.getDepartureTime(), LocalTime.of(8, 0));
        LocalTime t2 = parseTime(nextStop.getArrivalTime(), LocalTime.of(22, 0));

        double totalSecs = Math.max(1, t2.toSecondOfDay() - t1.toSecondOfDay());
        double elapsedSecs = Math.max(0, now.toSecondOfDay() - t1.toSecondOfDay());
        double fraction = elapsedSecs / totalSecs;

        double[] c1 = getCoordinates(currentStop.getStation().getCode());
        double[] c2 = getCoordinates(nextStop.getStation().getCode());

        double lat = c1[0] + fraction * (c2[0] - c1[0]);
        double lng = c1[1] + fraction * (c2[1] - c1[1]);

        int delay = nextStop.getExpectedDelayMinutes() != null ? nextStop.getExpectedDelayMinutes() : 0;

        return TrainLiveLocation.builder()
                .trainId(train.getId())
                .trainNumber(train.getTrainNumber())
                .trainName(train.getTrainName())
                .status("RUNNING")
                .lastStationCode(currentStop.getStation().getCode())
                .nextStationCode(nextStop.getStation().getCode())
                .latitude(lat)
                .longitude(lng)
                .currentSpeedKmph(75.0)
                .expectedDelayMinutes(delay)
                .build();
    }

    private double[] getCoordinates(String stationCode) {
        if (COORD_MAP.containsKey(stationCode)) {
            return COORD_MAP.get(stationCode);
        }
        // Fallback: Generate stable pseudo-random coordinates in India bounding box (Lat: 8 to 33, Lng: 68 to 90)
        double hashLat = 8.0 + (Math.abs(stationCode.hashCode()) % 25);
        double hashLng = 68.0 + (Math.abs((stationCode + "lng").hashCode()) % 22);
        return new double[]{hashLat, hashLng};
    }

    private LocalTime parseTime(String timeStr, LocalTime fallback) {
        if (timeStr == null || timeStr.trim().isEmpty()) return fallback;
        try {
            return LocalTime.parse(timeStr);
        } catch (Exception e) {
            return fallback;
        }
    }

    private TrainLiveLocation getDefaultLocation(Train train, String status, String last, String next, double lat, double lng) {
        return TrainLiveLocation.builder()
                .trainId(train.getId())
                .trainNumber(train.getTrainNumber())
                .trainName(train.getTrainName())
                .status(status)
                .lastStationCode(last)
                .nextStationCode(next)
                .latitude(lat)
                .longitude(lng)
                .currentSpeedKmph(0.0)
                .expectedDelayMinutes(0)
                .build();
    }
}
