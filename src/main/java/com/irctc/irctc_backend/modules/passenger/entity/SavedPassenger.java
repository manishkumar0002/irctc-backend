package com.irctc.irctc_backend.modules.passenger.entity;

import com.irctc.irctc_backend.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saved_passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "berth_preference")
    private String berthPreference; // LOWER, MIDDLE, UPPER, SIDE_LOWER, SIDE_UPPER, WINDOW
}
