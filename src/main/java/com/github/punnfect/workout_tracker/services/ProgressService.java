package com.github.punnfect.workout_tracker.services;

import com.github.punnfect.workout_tracker.dto.ExerciseProgressDto;
import com.github.punnfect.workout_tracker.dto.ExerciseProgressPointDto;
import com.github.punnfect.workout_tracker.dto.ExerciseProgressStatsDto;
import com.github.punnfect.workout_tracker.entities.ExerciseSet;
import com.github.punnfect.workout_tracker.entities.User;
import com.github.punnfect.workout_tracker.repository.ExerciseListRepo;
import com.github.punnfect.workout_tracker.repository.ExerciseSetRepo;
import com.github.punnfect.workout_tracker.repository.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final ExerciseSetRepo exerciseSetRepo;
    private final ExerciseListRepo exerciseListRepo;
    private final UserRepo userRepo;

    public ProgressService(ExerciseSetRepo exerciseSetRepo,
                           ExerciseListRepo exerciseListRepo,
                           UserRepo userRepo) {
        this.exerciseSetRepo = exerciseSetRepo;
        this.exerciseListRepo = exerciseListRepo;
        this.userRepo = userRepo;
    }

    //gets all exercise progress data for specified range
    @Transactional(readOnly = true)
    public ExerciseProgressDto getExerciseProgress(Long exerciseListId,
                                                   LocalDate startDate,
                                                   LocalDate endDate) {
        User currentUser = getCurrentUser();


        String exerciseName = exerciseListRepo.findById(exerciseListId)
                .map(ex -> ex.getName())
                .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseListId));


        List<ExerciseSet> sets;
        if (startDate != null && endDate != null) {
            sets = exerciseSetRepo.findExerciseProgressByDateRange(
                    exerciseListId, currentUser, startDate, endDate);
        } else {

            sets = exerciseSetRepo.findAllExerciseProgress(exerciseListId, currentUser);
        }

        // Convert to data points and only keep the best set per day
        // Best set = highest weight, then if tied, highest reps
        List<ExerciseProgressPointDto> dataPoints = sets.stream()
                .collect(Collectors.groupingBy(set -> set.getWorkout().getWorkoutDate()))
                .values()
                .stream()
                .map(setsPerDay -> {

                    ExerciseSet bestSet = setsPerDay.stream()
                            .max((s1, s2) -> {
                                int weightComparison = s1.getWeight().compareTo(s2.getWeight());
                                if (weightComparison != 0) {
                                    return weightComparison;
                                }
                                // If weights are equal, compare reps
                                return Integer.compare(s1.getReps(), s2.getReps());
                            })
                            .orElse(null);

                    if (bestSet == null) return null;

                    BigDecimal volume = bestSet.getWeight() != null && bestSet.getReps() != null
                            ? bestSet.getWeight().multiply(BigDecimal.valueOf(bestSet.getReps()))
                            : BigDecimal.ZERO;
                    return new ExerciseProgressPointDto(
                            bestSet.getWorkout().getWorkoutDate(),
                            bestSet.getWeight(),
                            bestSet.getReps(),
                            volume
                    );
                })
                .filter(point -> point != null)
                .sorted((p1, p2) -> p1.getDate().compareTo(p2.getDate()))
                .collect(Collectors.toList());

        // Calculate statistics
        ExerciseProgressStatsDto stats = calculateStats(sets);

        return new ExerciseProgressDto(exerciseName, dataPoints, stats);
    }

    //calculates summary stats
    private ExerciseProgressStatsDto calculateStats(List<ExerciseSet> sets) {
        if (sets.isEmpty()) {
            return new ExerciseProgressStatsDto();
        }

        ExerciseProgressStatsDto stats = new ExerciseProgressStatsDto();

        // Find max weight
        ExerciseSet maxWeightSet = sets.stream()
                .filter(s -> s.getWeight() != null)
                .max((s1, s2) -> s1.getWeight().compareTo(s2.getWeight()))
                .orElse(null);

        if (maxWeightSet != null) {
            stats.setMaxWeight(maxWeightSet.getWeight());
            stats.setMaxWeightReps(maxWeightSet.getReps());
            stats.setMaxWeightDate(maxWeightSet.getWorkout().getWorkoutDate());
        }

        // Find max reps
        ExerciseSet maxRepsSet = sets.stream()
                .filter(s -> s.getReps() != null)
                .max((s1, s2) -> Integer.compare(s1.getReps(), s2.getReps()))
                .orElse(null);

        if (maxRepsSet != null) {
            stats.setMaxReps(maxRepsSet.getReps());
            stats.setMaxRepsWeight(maxRepsSet.getWeight());
            stats.setMaxRepsDate(maxRepsSet.getWorkout().getWorkoutDate());
        }

        // Find max volume
        ExerciseSet maxVolumeSet = sets.stream()
                .filter(s -> s.getWeight() != null && s.getReps() != null)
                .max((s1, s2) -> {
                    BigDecimal vol1 = s1.getWeight().multiply(BigDecimal.valueOf(s1.getReps()));
                    BigDecimal vol2 = s2.getWeight().multiply(BigDecimal.valueOf(s2.getReps()));
                    return vol1.compareTo(vol2);
                })
                .orElse(null);

        if (maxVolumeSet != null) {
            stats.setMaxVolume(maxVolumeSet.getWeight()
                    .multiply(BigDecimal.valueOf(maxVolumeSet.getReps())));
            stats.setMaxVolumeWeight(maxVolumeSet.getWeight());
            stats.setMaxVolumeReps(maxVolumeSet.getReps());
            stats.setMaxVolumeDate(maxVolumeSet.getWorkout().getWorkoutDate());
        }

        return stats;
    }

    // helper method for getting user
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user '" + username + "' not found in the database"));
    }
}