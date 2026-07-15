package com.irctc.irctc_backend.modules.notification.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "notification_type", nullable = false)
    private String notificationType; // EMAIL, SMS, WHATSAPP, PUSH

    @Column(name = "event", nullable = false)
    private String event; // BOOKING, CONFIRMATION, CANCELLATION, REFUND, DELAY, etc.

    @Column(name = "subject")
    private String subject;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "status", nullable = false)
    private String status; // SENT, FAILED, PENDING

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
