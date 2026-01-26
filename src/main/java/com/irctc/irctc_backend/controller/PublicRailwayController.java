package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.service.RailwayLiveStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/railway")
@RequiredArgsConstructor
public class PublicRailwayController {

    private final RailwayLiveStatusService railwayService;

    // 🚆 LIVE TRAIN RUNNING STATUS
    @GetMapping("/train-status/{trainNumber}")
    public String getTrainLiveStatus(
            @PathVariable String trainNumber
    ) {
        return railwayService.getLiveTrainStatus(trainNumber);
    }
}
