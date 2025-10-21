package com.github.punnfect.workout_tracker.services;

import com.github.punnfect.workout_tracker.dto.CardioSessionDto;
import com.github.punnfect.workout_tracker.dto.ExerciseSetDto;
import com.github.punnfect.workout_tracker.dto.WorkoutSummaryDto;
import com.github.punnfect.workout_tracker.entities.*;
import com.github.punnfect.workout_tracker.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutRepo workoutRepo;
    private final UserRepo userRepo;
    private final CardioSessionRepo cardioSessionRepo;
    private final CardioListRepo cardioListRepo;
    private final ExerciseSetRepo exerciseSetRepo;
    private final ExerciseListRepo exerciseListRepo;

    public WorkoutService(WorkoutRepo workoutRepo, UserRepo userRepo, CardioSessionRepo cardioSessionRepo,
                          CardioListRepo cardioListRepo, ExerciseSetRepo exerciseSetRepo, ExerciseListRepo exerciseListRepo) {
        this.workoutRepo = workoutRepo;
        this.userRepo = userRepo;
        this.cardioSessionRepo = cardioSessionRepo;
        this.cardioListRepo = cardioListRepo;
        this.exerciseSetRepo = exerciseSetRepo;
        this.exerciseListRepo = exerciseListRepo;
    }

    // Creates a new base workout with only workoutDate to a user
    @Transactional
    public Workout createNewWorkout(LocalDate workoutDate, String title) {
        User currentUser = getCurrentUser();

        Workout newWorkout = new Workout();
        newWorkout.setUser(currentUser);
        newWorkout.setWorkoutDate(workoutDate);
        newWorkout.setTitle(title);

        return workoutRepo.save(newWorkout);
    }

    // Will save entire workout entered by user
    @Transactional
    public Workout saveWorkoutDetails(Long workoutId, String workoutNotes, LocalTime timeEnter, LocalTime timeLeave,
                                      List<ExerciseSetDto> exerciseSets, List<CardioSessionDto> cardioSessions) {

        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        workout.setNotes(workoutNotes);
        workout.setTimeEnter(timeEnter);
        workout.setTimeLeave(timeLeave);

        // Clear existing sets and sessions (more efficient than deleteAll)
        workout.getExerciseSets().clear();
        workout.getCardioSessions().clear();
        workoutRepo.flush(); // Ensure deletions are processed before adding new ones

        if (exerciseSets != null && !exerciseSets.isEmpty()) {
            for (ExerciseSetDto setDto : exerciseSets) {
                if (setDto == null || setDto.getExerciseListId() == null) {
                    continue;
                }

                ExerciseList exerciseType = exerciseListRepo.findById(setDto.getExerciseListId())
                        .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + setDto.getExerciseListId()));

                ExerciseSet newSet = new ExerciseSet();
                newSet.setWorkout(workout);
                newSet.setExerciseList(exerciseType);
                newSet.setSetNumber(setDto.getSetNumber());
                newSet.setWeight(setDto.getWeight());
                newSet.setReps(setDto.getReps());
                newSet.setNotes(setDto.getNotes());

                workout.getExerciseSets().add(newSet);
            }
        }

        if (cardioSessions != null && !cardioSessions.isEmpty()) {
            for (CardioSessionDto sessionDto : cardioSessions) {
                if (sessionDto == null || sessionDto.getCardioListId() == null) {
                    continue;
                }

                CardioList cardioType = cardioListRepo.findById(sessionDto.getCardioListId())
                        .orElseThrow(() -> new RuntimeException("Cardio activity not found with id: " + sessionDto.getCardioListId()));

                CardioSession newSession = new CardioSession();
                newSession.setWorkout(workout);
                newSession.setCardioList(cardioType);
                newSession.setDurationMinutes(sessionDto.getDurationMinutes());
                newSession.setDistance(sessionDto.getDistance());
                newSession.setNotes(sessionDto.getNotes());

                workout.getCardioSessions().add(newSession);
            }
        }

        return workoutRepo.save(workout);
    }

    // Returns entire workout history in summary form
    @Transactional(readOnly = true)
    public List<WorkoutSummaryDto> getWorkoutHistoryForCurrentUser() {
        User currentUser = getCurrentUser();
        List<Workout> workouts = workoutRepo.findByUserOrderByWorkoutDateDesc(currentUser);

        return workouts.stream()
                .map(workout -> new WorkoutSummaryDto(
                        workout.getId(),
                        workout.getWorkoutDate(),
                        workout.getTitle()))
                .collect(Collectors.toList());
    }

    // Returns the entire workout by ID with all related data (uses 2 queries to avoid MultipleBagFetchException)
    @Transactional(readOnly = true)
    public Optional<Workout> getWorkoutDetails(Long workoutId) {
        // Execute first query to fetch workout with exercise sets
        Optional<Workout> workoutOpt = workoutRepo.findByIdWithExerciseSets(workoutId);

        if (workoutOpt.isPresent()) {
            // Execute second query to fetch cardio sessions (Hibernate will merge into existing entity)
            workoutRepo.findByIdWithCardioSessions(workoutId);
        }

        return workoutOpt;
    }

    // Deletes an entire workout and all associated sets/sessions
    @Transactional
    public void deleteWorkout(Long workoutId) {
        workoutRepo.deleteById(workoutId);
    }

    // Deletes an exercise set from a workout
    @Transactional
    public void deleteExerciseSet(Long exerciseSetId) {
        exerciseSetRepo.deleteById(exerciseSetId);
    }

    // Deletes a cardio session from a workout
    @Transactional
    public void deleteCardioSession(Long cardioSessionId) {
        cardioSessionRepo.deleteById(cardioSessionId);
    }

    // Helper function for matching a user by their username
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