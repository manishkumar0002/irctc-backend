package com.irctc.irctc_backend.modules.passenger.repository;

import com.irctc.irctc_backend.entity.User;
import com.irctc.irctc_backend.modules.passenger.entity.SavedPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedPassengerRepository extends JpaRepository<SavedPassenger, Long> {
    List<SavedPassenger> findByUser(User user);
    List<SavedPassenger> findByUserId(Long userId);
}
