package com.github.punnfect.workout_tracker.services;

import com.github.punnfect.workout_tracker.dto.CardioSessionDto;
import com.github.punnfect.workout_tracker.dto.ExerciseSetDto;
import com.github.punnfect.workout_tracker.entities.*;
import com.github.punnfect.workout_tracker.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
    public Workout createNewWorkout(LocalDate workoutDate) {

        User currentUser = getCurrentUser();

        Workout newWorkout = new Workout();
        newWorkout.setUser(currentUser);
        newWorkout.setWorkoutDate(workoutDate);

        return workoutRepo.save(newWorkout);
    }

    //Will save entire workout entered by user
    @Transactional
    public Workout saveWorkoutDetails(Long workoutId, LocalTime timeEnter, LocalTime timeLeave,
                                      List<ExerciseSetDto> exerciseSets, List<CardioSessionDto> cardioSessions) {

        //finds workout to attach all info too
        Workout workout = workoutRepo.findById(workoutId)
                .orElseThrow(() -> new RuntimeException("Workout not found with id: " + workoutId));

        //set your times
        workout.setTimeEnter(timeEnter);
        workout.setTimeLeave(timeLeave);

        //gets all exercise sets and adds them to the workouts list
        for (ExerciseSetDto setDto : exerciseSets) {
            ExerciseList exerciseType = exerciseListRepo.findById(setDto.getExerciseListId())
                    .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + setDto.getExerciseListId()));

            ExerciseSet newSet = new ExerciseSet();
            newSet.setWorkout(workout);
            newSet.setExerciseList(exerciseType);
            newSet.setSetNumber(setDto.getSetNumber());
            newSet.setWeight(setDto.getWeight());
            newSet.setReps(setDto.getReps());
            exerciseSetRepo.save(newSet);
        }

        //gets all cardio sessions and adds them to the workouts list
        for (CardioSessionDto sessionDto : cardioSessions) {
            CardioList cardioType = cardioListRepo.findById(sessionDto.getCardioListId())
                    .orElseThrow(() -> new RuntimeException("Cardio activity not found with id: " + sessionDto.getCardioListId()));

            CardioSession newSession = new CardioSession();
            newSession.setWorkout(workout);
            newSession.setCardioList(cardioType);
            newSession.setDurationMinutes(sessionDto.getDurationMinutes());
            newSession.setDistance(sessionDto.getDistance());
            cardioSessionRepo.save(newSession);
        }

        return workoutRepo.save(workout);
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
