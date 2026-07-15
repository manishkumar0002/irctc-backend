package com.irctc.irctc_backend.modules.timeline.repository;

import com.irctc.irctc_backend.modules.timeline.entity.ActivityTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityTimelineRepository extends JpaRepository<ActivityTimeline, Long> {
    List<ActivityTimeline> findByBookingIdOrderByTimestampAsc(Long bookingId);
    void deleteByBookingId(Long bookingId);
}
