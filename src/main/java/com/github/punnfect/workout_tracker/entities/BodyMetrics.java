package com.github.punnfect.workout_tracker.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "body_metrics")
public class BodyMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;

    @Column(name = "weight_lbs", precision = 5, scale = 2)
    private BigDecimal weightLbs;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
