package com.irctc.irctc_backend.controller;

import com.irctc.irctc_backend.entity.Station;
import com.irctc.irctc_backend.repository.StationRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/admin/stations")
@AllArgsConstructor
public class AdminStationController {

    private final StationRepository stationRepository;

    @PostMapping
    public Station addStation(@RequestBody Station station) {
        return stationRepository.save(station);
    }

    @GetMapping
    public List<Station> getAllStations() {
        return stationRepository.findAll();
    }
}
