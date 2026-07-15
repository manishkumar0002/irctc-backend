package com.irctc.irctc_backend.modules.coach.controller;

import com.irctc.irctc_backend.modules.coach.dto.CoachRequest;
import com.irctc.irctc_backend.modules.coach.dto.CoachResponse;
import com.irctc.irctc_backend.modules.coach.service.CoachService;
import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/v1/coaches")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    @PostMapping
    public ResponseEntity<ApiResponse<CoachResponse>> createCoach(@Valid @RequestBody CoachRequest request) {
        CoachResponse data = coachService.createCoach(request);
        return ResponseEntity.ok(ApiResponse.success("Coach created successfully", data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CoachResponse>> updateCoach(
            @PathVariable Long id,
            @Valid @RequestBody CoachRequest request
    ) {
        CoachResponse data = coachService.updateCoach(id, request);
        return ResponseEntity.ok(ApiResponse.success("Coach updated successfully", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return ResponseEntity.ok(ApiResponse.success("Coach deleted successfully", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CoachResponse>> getCoachById(@PathVariable Long id) {
        CoachResponse data = coachService.getCoachById(id);
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CoachResponse>>> getAllCoaches() {
        List<CoachResponse> data = coachService.getAllCoaches();
        return ResponseEntity.ok(ApiResponse.success(data));
    }

    @PostMapping("/{coachId}/attach/{trainId}")
    public ResponseEntity<ApiResponse<CoachResponse>> attachCoach(
            @PathVariable Long coachId,
            @PathVariable Long trainId,
            @RequestParam(defaultValue = "1") Integer position
    ) {
        CoachResponse data = coachService.attachCoachToTrain(coachId, trainId, position);
        return ResponseEntity.ok(ApiResponse.success("Coach attached to train successfully", data));
    }

    @PostMapping("/{coachId}/detach")
    public ResponseEntity<ApiResponse<CoachResponse>> detachCoach(@PathVariable Long coachId) {
        CoachResponse data = coachService.detachCoachFromTrain(coachId);
        return ResponseEntity.ok(ApiResponse.success("Coach detached from train successfully", data));
    }
}
