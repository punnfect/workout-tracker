package com.github.punnfect.workout_tracker.services;

import com.github.punnfect.workout_tracker.dto.CardioSessionDto;
import com.github.punnfect.workout_tracker.dto.ExerciseSetDto;
import com.github.punnfect.workout_tracker.dto.WorkoutSummaryDto;
import com.github.punnfect.workout_tracker.entities.*;
import com.github.punnfect.workout_tracker.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    public WorkoutService(WorkoutRepo workoutRepo, UserRepo userRepo, CardioSessionRepo cardioSessionRepo, CardioListRepo cardioListRepo, ExerciseSetRepo exerciseSetRepo,  ExerciseListRepo exerciseListRepo) {
        this.workoutRepo = workoutRepo;
        this.userRepo = userRepo;
        this.cardioSessionRepo = cardioSessionRepo;
        this.cardioListRepo = cardioListRepo;
        this.exerciseSetRepo = exerciseSetRepo;
        this.exerciseListRepo = exerciseListRepo;
    }

    //Creates a new base workout with only workoutDate to a user
    @Transactional
    public Workout createNewWorkout(LocalDate workoutDate, String title) {

        User currentUser = getCurrentUser();

        Workout newWorkout = new Workout();
        newWorkout.setUser(currentUser);
        newWorkout.setWorkoutDate(workoutDate);
        newWorkout.setTitle(title);

        return workoutRepo.save(newWorkout);
    }

    //Will save entire workout entered by user
    @Transactional
    public Workout saveWorkoutDetails(Long workoutId, String workoutNotes, LocalTime timeEnter, LocalTime timeLeave,
                                      List<ExerciseSetDto> exerciseSets, List<CardioSessionDto> cardioSessions) {

        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        workout.setNotes(workoutNotes);
        workout.setTimeEnter(timeEnter);
        workout.setTimeLeave(timeLeave);

        exerciseSetRepo.deleteAll(workout.getExerciseSets());
        cardioSessionRepo.deleteAll(workout.getCardioSessions());

        for (ExerciseSetDto setDto : exerciseSets) {
            ExerciseList exerciseType = exerciseListRepo.findById(setDto.getExerciseListId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + setDto.getExerciseListId()));
            ExerciseSet newSet = new ExerciseSet();
            newSet.setWorkout(workout);
            newSet.setExerciseList(exerciseType);
            newSet.setSetNumber(setDto.getSetNumber());
            newSet.setWeight(setDto.getWeight());
            newSet.setReps(setDto.getReps());
            newSet.setNotes(setDto.getNotes());
            exerciseSetRepo.save(newSet);
        }

        for (CardioSessionDto sessionDto : cardioSessions) {
            CardioList cardioType = cardioListRepo.findById(sessionDto.getCardioListId())
                    .orElseThrow(() -> new RuntimeException("Cardio activity not found with id: " + sessionDto.getCardioListId()));
            CardioSession newSession = new CardioSession();
            newSession.setWorkout(workout);
            newSession.setCardioList(cardioType);
            newSession.setDurationMinutes(sessionDto.getDurationMinutes());
            newSession.setDistance(sessionDto.getDistance());
            newSession.setNotes(sessionDto.getNotes());
            cardioSessionRepo.save(newSession);
        }

        return workoutRepo.save(workout);
    }

    //returns entire workout history in summary form
    public List<WorkoutSummaryDto> getWorkoutHistoryForCurrentUser() {
        User currentUser = getCurrentUser();
        //get entire workout list
        List<Workout> workouts = workoutRepo.findByUserOrderByWorkoutDateDesc(currentUser);

        //return a list of the summaries of workouts
        return workouts.stream()
                .map(workout -> new WorkoutSummaryDto(
                        workout.getId(),
                        workout.getWorkoutDate(),
                        workout.getTitle()))
                .collect(Collectors.toList());
    }

    //returns the entire workout by ID
    public Optional<Workout> getWorkoutDetails(Long workoutId) {
        return workoutRepo.findById(workoutId);
    }

    //deletes an entire workout and all associated sets/sessions
    @Transactional
    public void deleteWorkout(Long workoutId) {
        workoutRepo.deleteById(workoutId);
    }

    //deletes an exercise set from a workout
    @Transactional
    public void deleteExerciseSet(Long exerciseSetId) {
        exerciseSetRepo.deleteById(exerciseSetId);
    }

    //deletes a cardio session from a workout
    @Transactional
    public void deleteCardioSession(Long cardioSessionId) {
        cardioSessionRepo.deleteById(cardioSessionId);
    }














    //Helper function for matching a user by their username
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
