package com.github.punnfect.workout_tracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ExerciseSetDto {
    @NotNull
    private Long exerciseListId;

    private int setNumber;

    @NotNull(message = "Weight is required")
    private BigDecimal weight;

    @NotNull(message = "Reps are required")
    @Min(value = 1, message = "Reps must be at least 1")
    private Integer reps;

    private String notes;
}