package com.github.punnfect.workout_tracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// complete progress info including a list of all datapoints
@Data
@AllArgsConstructor
public class ExerciseProgressDto {
    private String exerciseName;
    private List<ExerciseProgressPointDto> dataPoints;
    private ExerciseProgressStatsDto stats;
}