package com.irctc.irctc_backend.modules.timeline.controller;

import com.irctc.irctc_backend.modules.common.dto.ApiResponse;
import com.irctc.irctc_backend.modules.timeline.entity.ActivityTimeline;
import com.irctc.irctc_backend.modules.timeline.service.ActivityTimelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final ActivityTimelineService timelineService;

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<List<ActivityTimeline>>> getBookingTimeline(@PathVariable Long bookingId) {
        List<ActivityTimeline> data = timelineService.getTimelineForBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
