package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.dto.TrainRequest;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/trains")
@RequiredArgsConstructor
public class AdminTrainController {

    private final TrainService trainService;

    // ADMIN: Add train
    @PostMapping
    public Train addTrain(@RequestBody TrainRequest request) {
        return trainService.addTrain(request);
    }

    // ADMIN: View all trains
    @GetMapping
    public List<Train> getAllTrains() {
        return trainService.getAllTrains();
    }
}
