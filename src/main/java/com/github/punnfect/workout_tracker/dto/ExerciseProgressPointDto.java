package com.github.punnfect.workout_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

// represents a single progress point on chart
@Data
@AllArgsConstructor
public class ExerciseProgressPointDto {
    private LocalDate date;
    private BigDecimal weight;
    private Integer reps;
    private BigDecimal volume;
}