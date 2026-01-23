package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.dto.TrainRequest;
import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.service.TrainService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    //  POST: Add Train
   // @PostMapping
    //public Train addTrain(@RequestBody TrainRequest request) {
    //    return trainService.addTrain(request);
   // }

    //  GET: Fetch All Trains
    @GetMapping
    public List<Train> getAllTrains() {
        return trainService.getAllTrains();
    }
}
