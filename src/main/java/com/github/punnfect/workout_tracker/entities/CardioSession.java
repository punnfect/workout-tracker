package com.github.punnfect.workout_tracker.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cardio_sessions")
public class CardioSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cardio_session_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "cardio_list_id", nullable = false)
    private CardioList cardioList;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "distance", precision = 5, scale = 2)
    private BigDecimal distance;

    @Column(name = "notes", length = 255)
    private String notes;
}
