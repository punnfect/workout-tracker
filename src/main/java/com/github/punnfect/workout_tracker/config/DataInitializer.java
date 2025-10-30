package com.github.punnfect.workout_tracker.config;

import com.github.punnfect.workout_tracker.entities.*;
import com.github.punnfect.workout_tracker.repository.CardioListRepo;
import com.github.punnfect.workout_tracker.repository.ExerciseListRepo;
import com.github.punnfect.workout_tracker.repository.UserRepo;
import com.github.punnfect.workout_tracker.repository.WorkoutRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Used for initializing a username/password. Can change later
Also adds initial info for exercise/cardio lists
*/
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ExerciseListRepo exerciseListRepo;
    private final CardioListRepo cardioListRepo;
    private final WorkoutRepo workoutRepo;

    public DataInitializer(UserRepo userRepo, PasswordEncoder passwordEncoder, ExerciseListRepo exerciseListRepo, CardioListRepo cardioListRepo,  WorkoutRepo workoutRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.exerciseListRepo = exerciseListRepo;
        this.cardioListRepo = cardioListRepo;
        this.workoutRepo = workoutRepo;
    }

    @Override
    public void run(String... args) throws Exception {

        // --- Create Initial User ---
        User adminUser = null;
        if (userRepo.count() == 0) {
            log.info("No users found in the database. Creating the initial admin user.");

            adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("password"));
            userRepo.save(adminUser);

            log.info("Initial admin user created successfully.");
        } else {
            adminUser = userRepo.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("DataInitializer: Admin user exists but could not be found."));
            log.info("Users already exist in the database. Skipping initial user creation.");
        }


    }

}