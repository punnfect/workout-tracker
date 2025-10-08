package com.github.punnfect.workout_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ExerciseSetDto {
    private Long exerciseListId;
    private int setNumber;
    private BigDecimal weight;
    private int reps;
    private String notes;
}