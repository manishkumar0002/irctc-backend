package com.irctc.irctc_backend.modules.passenger.controller;

import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import com.irctc.irctc_backend.modules.passenger.entity.SavedPassenger;
import com.irctc.irctc_backend.modules.passenger.service.SavedPassengerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/passengers/saved")
@RequiredArgsConstructor
public class SavedPassengerController {

    private final SavedPassengerService savedPassengerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SavedPassenger>>> getSavedPassengers(Principal principal) {
        List<SavedPassenger> list = savedPassengerService.getSavedPassengers(principal.getName());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SavedPassenger>> savePassenger(Principal principal, @RequestBody SavedPassenger passenger) {
        SavedPassenger saved = savedPassengerService.savePassenger(principal.getName(), passenger);
        return ResponseEntity.ok(ApiResponse.success("Passenger profile saved successfully", saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SavedPassenger>> updatePassenger(@PathVariable Long id, @RequestBody SavedPassenger passenger) {
        SavedPassenger updated = savedPassengerService.updatePassenger(id, passenger);
        return ResponseEntity.ok(ApiResponse.success("Passenger profile updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePassenger(@PathVariable Long id) {
        savedPassengerService.deletePassenger(id);
        return ResponseEntity.ok(ApiResponse.success("Passenger profile deleted successfully", null));
    }
}
