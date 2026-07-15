package com.irctc.irctc_backend.modules.coach.repository;

import com.irctc.irctc_backend.modules.coach.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    List<Coach> findByTrainId(Long trainId);
    List<Coach> findByTrainIdOrderByCoachPositionAsc(Long trainId);
}
