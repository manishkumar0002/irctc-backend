package com.irctc.irctc_backend.modules.schedule.controller;

import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.entity.TrainRoute;
import com.irctc.irctc_backend.entity.TrainStop;
import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import com.irctc.irctc_backend.repository.TrainRepository;
import com.irctc.irctc_backend.repository.TrainRouteRepository;
import com.irctc.irctc_backend.repository.TrainStopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;

@RestController
@RequiredArgsConstructor
public class TrainScheduleController {

    private final TrainRepository trainRepository;
    private final TrainRouteRepository trainRouteRepository;
    private final TrainStopRepository trainStopRepository;
    private final CacheManager cacheManager;

    // Public Schedule View API
    @Cacheable(value = "schedules", key = "#trainId")
    @GetMapping("/api/public/v1/trains/{trainId}/schedule")
    public ResponseEntity<ApiResponse<List<TrainStop>>> getTrainSchedule(@PathVariable Long trainId) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        TrainRoute route = trainRouteRepository.findByTrain(train)
                .orElseThrow(() -> new RuntimeException("Route not defined for train"));

        List<TrainStop> schedule = trainStopRepository.findByRouteOrderByStopOrder(route);
        return ResponseEntity.ok(ApiResponse.success(schedule));
    }

    // Admin Schedule Update API
    @PutMapping("/api/admin/v1/schedules/stops/{stopId}")
    public ResponseEntity<ApiResponse<TrainStop>> updateStopScheduleDetails(
            @PathVariable Long stopId,
            @RequestParam(required = false) String arrivalTime,
            @RequestParam(required = false) String departureTime,
            @RequestParam(required = false) Integer platform,
            @RequestParam(required = false) Double distanceFromOrigin,
            @RequestParam(required = false) String runningDays,
            @RequestParam(required = false, defaultValue = "0") Integer expectedDelayMinutes
    ) {
        TrainStop stop = trainStopRepository.findById(stopId)
                .orElseThrow(() -> new RuntimeException("Train stop not found with ID: " + stopId));

        if (arrivalTime != null) stop.setArrivalTime(arrivalTime);
        if (departureTime != null) stop.setDepartureTime(departureTime);
        if (platform != null) stop.setPlatform(platform);
        if (distanceFromOrigin != null) stop.setDistanceFromOrigin(distanceFromOrigin);
        if (runningDays != null) stop.setRunningDays(runningDays);
        if (expectedDelayMinutes != null) stop.setExpectedDelayMinutes(expectedDelayMinutes);

        TrainStop updated = trainStopRepository.save(stop);

        // Evict from Redis cache
        if (updated.getRoute() != null && updated.getRoute().getTrain() != null) {
            Long trainId = updated.getRoute().getTrain().getId();
            var cache = cacheManager.getCache("schedules");
            if (cache != null) {
                cache.evict(trainId);
            }
        }

        return ResponseEntity.ok(ApiResponse.success("Schedule stop details updated successfully", updated));
    }
}
