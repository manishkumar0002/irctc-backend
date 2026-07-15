package com.irctc.irctc_backend.modules.timeline.service;

import com.irctc.irctc_backend.entity.Booking;
import com.irctc.irctc_backend.modules.timeline.entity.ActivityTimeline;
import com.irctc.irctc_backend.modules.timeline.repository.ActivityTimelineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityTimelineService {

    private final ActivityTimelineRepository timelineRepository;

    @Transactional
    public void addEvent(Booking booking, String eventName, String description) {
        ActivityTimeline timeline = ActivityTimeline.builder()
                .booking(booking)
                .eventName(eventName.toUpperCase())
                .description(description)
                .timestamp(LocalDateTime.now())
                .build();
        timelineRepository.save(timeline);
    }

    public List<ActivityTimeline> getTimelineForBooking(Long bookingId) {
        return timelineRepository.findByBookingIdOrderByTimestampAsc(bookingId);
    }
}
