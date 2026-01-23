package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.entity.Train;
import com.irctc.irctc_backend.service.TrainSearchService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trains/search")
@AllArgsConstructor
public class TrainSearchController {
    private final TrainSearchService trainSearchService;

    @GetMapping
    public List<Train> search(
            @RequestParam String source,
            @RequestParam String destination
    ){
        return trainSearchService.searchTrains(source,destination);
    }
}
