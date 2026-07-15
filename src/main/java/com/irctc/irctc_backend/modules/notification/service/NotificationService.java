package com.irctc.irctc_backend.modules.notification.service;

import com.irctc.irctc_backend.modules.notification.entity.NotificationHistory;
import com.irctc.irctc_backend.modules.notification.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationHistoryRepository notificationHistoryRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public NotificationHistory sendNotification(
            String recipient,
            String type, // EMAIL, SMS, WHATSAPP, PUSH
            String event,
            String subject,
            String content
    ) {
        NotificationHistory history = NotificationHistory.builder()
                .recipient(recipient)
                .notificationType(type.toUpperCase())
                .event(event.toUpperCase())
                .subject(subject)
                .content(content)
                .status("PENDING")
                .retryCount(0)
                .timestamp(LocalDateTime.now())
                .build();

        NotificationHistory saved = notificationHistoryRepository.save(history);
        dispatch(saved);
        return saved;
    }

    @Transactional
    public void retryFailedNotifications() {
        List<NotificationHistory> failedList = notificationHistoryRepository
                .findByStatusAndRetryCountLessThan("FAILED", 3);

        for (NotificationHistory item : failedList) {
            item.setRetryCount(item.getRetryCount() + 1);
            dispatch(item);
        }
    }

    private void dispatch(NotificationHistory history) {
        try {
            if ("EMAIL".equalsIgnoreCase(history.getNotificationType())) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(history.getRecipient());
                message.setSubject(history.getSubject() != null ? history.getSubject() : "IRCTC Notification");
                message.setText(history.getContent());
                mailSender.send(message);
            } else {
                // SMS / WHATSAPP / PUSH - Mock dispatching logging to console
                System.out.println("Dispatched mock " + history.getNotificationType() + 
                                   " alert to " + history.getRecipient() + ": " + history.getContent());
            }
            history.setStatus("SENT");
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
            history.setStatus("FAILED");
        }
        notificationHistoryRepository.save(history);
    }
}
