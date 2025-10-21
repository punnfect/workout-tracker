package com.github.punnfect.workout_tracker.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExerciseProgressDto {

    private LocalDate date;
    private BigDecimal weight;
    private Integer reps;
}
