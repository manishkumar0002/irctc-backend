package com.irctc.irctc_backend.modules.notification.repository;

import com.irctc.irctc_backend.modules.notification.entity.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {
    List<NotificationHistory> findByStatus(String status);
    List<NotificationHistory> findByRecipient(String recipient);
    List<NotificationHistory> findByStatusAndRetryCountLessThan(String status, int maxRetryCount);
}
