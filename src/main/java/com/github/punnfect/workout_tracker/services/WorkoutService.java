package com.github.punnfect.workout_tracker.services;

import com.github.punnfect.workout_tracker.entities.User;
import com.github.punnfect.workout_tracker.entities.Workout;
import com.github.punnfect.workout_tracker.repository.CardioSessionRepo;
import com.github.punnfect.workout_tracker.repository.ExerciseSetRepo;
import com.github.punnfect.workout_tracker.repository.UserRepo;
import com.github.punnfect.workout_tracker.repository.WorkoutRepo;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WorkoutService {

    private final WorkoutRepo workoutRepo;
    private final UserRepo userRepo;
    private final CardioSessionRepo cardioSessionRepo;
    private final ExerciseSetRepo exerciseSetRepo;

    public WorkoutService(WorkoutRepo workoutRepo, UserRepo userRepo, CardioSessionRepo cardioSessionRepo, ExerciseSetRepo exerciseSetRepo) {
        this.workoutRepo = workoutRepo;
        this.userRepo = userRepo;
        this.cardioSessionRepo = cardioSessionRepo;
        this.exerciseSetRepo = exerciseSetRepo;
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
