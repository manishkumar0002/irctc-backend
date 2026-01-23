package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.entity.Station;
import com.irctc.irctc_backend.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationRepository stationRepository;

    //  USER + ADMIN
    @GetMapping
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }
}
