package com.github.punnfect.workout_tracker.config;

import com.github.punnfect.workout_tracker.entities.CardioList;
import com.github.punnfect.workout_tracker.entities.ExerciseList;
import com.github.punnfect.workout_tracker.entities.User;
import com.github.punnfect.workout_tracker.repository.CardioListRepo;
import com.github.punnfect.workout_tracker.repository.ExerciseListRepo;
import com.github.punnfect.workout_tracker.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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

    public DataInitializer(UserRepo userRepo, PasswordEncoder passwordEncoder, ExerciseListRepo exerciseListRepo, CardioListRepo cardioListRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.exerciseListRepo = exerciseListRepo;
        this.cardioListRepo = cardioListRepo;
    }

    @Override
    public void run(String... args) throws Exception {

        //creates initial user if none exist for testing
        if (userRepo.count() == 0) {
            log.info("No users found in the database. Creating the initial admin user.");

            User adminUser = new User();
            adminUser.setUsername("admin");

            adminUser.setPassword(passwordEncoder.encode("password"));
            userRepo.save(adminUser);

            log.info("Initial admin user created successfully.");
        } else {
            log.info("Users already exist in the database. Skipping initial user creation.");
        }

        // Create initial exercises if none exist for testing
        if (exerciseListRepo.count() == 0) {
            log.info("Creating initial exercises...");
            ExerciseList bench = new ExerciseList();
            bench.setName("Bench Press");

            ExerciseList squat = new ExerciseList();
            squat.setName("Squat");

            ExerciseList deadlift = new ExerciseList();
            deadlift.setName("Deadlift");

            exerciseListRepo.saveAll(Arrays.asList(bench, squat, deadlift));
            log.info("Initial exercises created.");
        }

        // Create initial cardio activities if none exist for testing
        if (cardioListRepo.count() == 0) {
            log.info("Creating initial cardio activities...");
            CardioList treadmill = new CardioList();
            treadmill.setName("Treadmill");

            CardioList cycling = new CardioList();
            cycling.setName("Cycling");

            cardioListRepo.saveAll(Arrays.asList(treadmill, cycling));
            log.info("Initial cardio activities created.");
        }

    }
}