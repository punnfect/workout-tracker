package com.github.punnfect.workout_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// contains all stats for stats table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseProgressStatsDto {
    // max weight first reps second
    private BigDecimal maxWeight;
    private Integer maxWeightReps;
    private LocalDate maxWeightDate;

    // max reps first weight second
    private BigDecimal maxRepsWeight;
    private Integer maxReps;
    private LocalDate maxRepsDate;

    // max volume on a single set
    private BigDecimal maxVolume;
    private BigDecimal maxVolumeWeight;
    private Integer maxVolumeReps;
    private LocalDate maxVolumeDate;
}
