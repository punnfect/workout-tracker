package com.github.punnfect.workout_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class WorkoutSummaryDto {
    private Long id;
    private LocalDate workoutDate;
    private String title;
}
