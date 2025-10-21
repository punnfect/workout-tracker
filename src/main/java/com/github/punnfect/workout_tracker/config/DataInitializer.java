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

        // --- Create Initial Exercises ---
        ExerciseList bench = null, squat = null, deadlift = null, pullup = null, curl = null;

        if (exerciseListRepo.count() == 0) {
            log.info("Creating initial exercises...");
            bench = new ExerciseList();
            bench.setName("Bench Press");

            squat = new ExerciseList();
            squat.setName("Squat");

            deadlift = new ExerciseList();
            deadlift.setName("Deadlift");

            pullup = new ExerciseList();
            pullup.setName("Overhand Pull-up");

            curl = new ExerciseList();
            curl.setName("Bicep Curl");

            // Save and re-assign to get the managed entities
            List<ExerciseList> savedExercises = exerciseListRepo.saveAll(Arrays.asList(bench, squat, deadlift, pullup, curl));
            bench = savedExercises.stream().filter(e -> e.getName().equals("Bench Press")).findFirst().orElse(bench);
            squat = savedExercises.stream().filter(e -> e.getName().equals("Squat")).findFirst().orElse(squat);
            deadlift = savedExercises.stream().filter(e -> e.getName().equals("Deadlift")).findFirst().orElse(deadlift);
            pullup = savedExercises.stream().filter(e -> e.getName().equals("Overhand Pull-up")).findFirst().orElse(pullup);
            curl = savedExercises.stream().filter(e -> e.getName().equals("Bicep Curl")).findFirst().orElse(curl);

            log.info("Initial exercises created.");
        } else {
            // Fetch them if they already exist
            bench = exerciseListRepo.findAll().stream().filter(e -> e.getName().equals("Bench Press")).findFirst().orElse(null);
            squat = exerciseListRepo.findAll().stream().filter(e -> e.getName().equals("Squat")).findFirst().orElse(null);
            deadlift = exerciseListRepo.findAll().stream().filter(e -> e.getName().equals("Deadlift")).findFirst().orElse(null);
            pullup = exerciseListRepo.findAll().stream().filter(e -> e.getName().equals("Overhand Pull-up")).findFirst().orElse(null);
            curl = exerciseListRepo.findAll().stream().filter(e -> e.getName().equals("Bicep Curl")).findFirst().orElse(null);
        }


        // --- Create Initial Cardio ---
        CardioList treadmill = null, cycling = null, rowing = null;

        if (cardioListRepo.count() == 0) {
            log.info("Creating initial cardio activities...");
            treadmill = new CardioList();
            treadmill.setName("Treadmill");

            cycling = new CardioList();
            cycling.setName("Cycling");

            rowing = new CardioList();
            rowing.setName("Rowing");

            // Save and re-assign
            List<CardioList> savedCardio = cardioListRepo.saveAll(Arrays.asList(treadmill, cycling, rowing));
            treadmill = savedCardio.stream().filter(c -> c.getName().equals("Treadmill")).findFirst().orElse(treadmill);
            cycling = savedCardio.stream().filter(c -> c.getName().equals("Cycling")).findFirst().orElse(cycling);
            rowing = savedCardio.stream().filter(c -> c.getName().equals("Rowing")).findFirst().orElse(rowing);

            log.info("Initial cardio activities created.");
        } else {
            // Fetch them if they already exist
            treadmill = cardioListRepo.findAll().stream().filter(c -> c.getName().equals("Treadmill")).findFirst().orElse(null);
            cycling = cardioListRepo.findAll().stream().filter(c -> c.getName().equals("Cycling")).findFirst().orElse(null);
            rowing = cardioListRepo.findAll().stream().filter(c -> c.getName().equals("Rowing")).findFirst().orElse(null);
        }


        // --- Create Sample Workouts ---
        if (workoutRepo.count() == 0) {
            log.info("Creating 7 sample workouts...");
            List<Workout> sampleWorkouts = new ArrayList<>();

            // Workout 1: Chest Day
            Workout w1 = new Workout();
            w1.setUser(adminUser);
            w1.setTitle("Chest & Triceps");
            w1.setWorkoutDate(LocalDate.now().minusDays(10));
            w1.setTimeEnter(LocalTime.of(17, 0));
            w1.setTimeLeave(LocalTime.of(18, 15));
            w1.setNotes("Felt strong today. Good pump.");
            w1.setExerciseSets(new ArrayList<>());
            if (bench != null) {
                w1.getExerciseSets().add(createSet(w1, bench, 1, new BigDecimal("135.0"), 10, "Warm-up"));
                w1.getExerciseSets().add(createSet(w1, bench, 2, new BigDecimal("185.0"), 8, null));
                w1.getExerciseSets().add(createSet(w1, bench, 3, new BigDecimal("205.0"), 5, "Felt heavy"));
                w1.getExerciseSets().add(createSet(w1, bench, 4, new BigDecimal("185.0"), 8, null));
            }
            sampleWorkouts.add(w1);

            // Workout 2: Leg Day
            Workout w2 = new Workout();
            w2.setUser(adminUser);
            w2.setTitle("Leg Day");
            w2.setWorkoutDate(LocalDate.now().minusDays(8));
            w2.setTimeEnter(LocalTime.of(18, 0));
            w2.setTimeLeave(LocalTime.of(19, 30));
            w2.setNotes("Brutal session. Squats felt good but deadlifts were tough.");
            w2.setExerciseSets(new ArrayList<>());
            if (squat != null) {
                w2.getExerciseSets().add(createSet(w2, squat, 1, new BigDecimal("135.0"), 10, "Warm-up"));
                w2.getExerciseSets().add(createSet(w2, squat, 2, new BigDecimal("225.0"), 5, null));
                w2.getExerciseSets().add(createSet(w2, squat, 3, new BigDecimal("245.0"), 5, null));
                w2.getExerciseSets().add(createSet(w2, squat, 4, new BigDecimal("245.0"), 5, "Grind"));
            }
            if (deadlift != null) {
                w2.getExerciseSets().add(createSet(w2, deadlift, 1, new BigDecimal("225.0"), 5, null));
                w2.getExerciseSets().add(createSet(w2, deadlift, 2, new BigDecimal("275.0"), 3, null));
            }
            sampleWorkouts.add(w2);

            // Workout 3: Cardio Day
            Workout w3 = new Workout();
            w3.setUser(adminUser);
            w3.setTitle("Cardio & Core");
            w3.setWorkoutDate(LocalDate.now().minusDays(7));
            w3.setTimeEnter(LocalTime.of(7, 0));
            w3.setTimeLeave(LocalTime.of(7, 45));
            w3.setNotes("Easy morning run.");
            w3.setCardioSessions(new ArrayList<>());
            if (treadmill != null) {
                w3.getCardioSessions().add(createCardio(w3, treadmill, 30, new BigDecimal("3.1"), "5k run"));
            }
            sampleWorkouts.add(w3);

            // Workout 4: Back & Biceps
            Workout w4 = new Workout();
            w4.setUser(adminUser);
            w4.setTitle("Back & Biceps");
            w4.setWorkoutDate(LocalDate.now().minusDays(5));
            w4.setTimeEnter(LocalTime.of(17, 30));
            w4.setTimeLeave(LocalTime.of(18, 45));
            w4.setNotes("");
            w4.setExerciseSets(new ArrayList<>());
            if (pullup != null) {
                w4.getExerciseSets().add(createSet(w4, pullup, 1, new BigDecimal("0.0"), 8, "Bodyweight"));
                w4.getExerciseSets().add(createSet(w4, pullup, 2, new BigDecimal("0.0"), 6, null));
                w4.getExerciseSets().add(createSet(w4, pullup, 3, new BigDecimal("0.0"), 5, null));
            }
            if (curl != null) {
                w4.getExerciseSets().add(createSet(w4, curl, 1, new BigDecimal("30.0"), 12, "Dumbbell curls"));
                w4.getExerciseSets().add(createSet(w4, curl, 2, new BigDecimal("30.0"), 10, null));
                w4.getExerciseSets().add(createSet(w4, curl, 3, new BigDecimal("35.0"), 8, null));
            }
            w4.setCardioSessions(new ArrayList<>());
            if (rowing != null) {
                w4.getCardioSessions().add(createCardio(w4, rowing, 10, new BigDecimal("2.0"), "Warm-up row"));
            }
            sampleWorkouts.add(w4);

            // Workout 5: Full Body Quick
            Workout w5 = new Workout();
            w5.setUser(adminUser);
            w5.setTitle("Quick Full Body");
            w5.setWorkoutDate(LocalDate.now().minusDays(3));
            w5.setTimeEnter(LocalTime.of(12, 0));
            w5.setTimeLeave(LocalTime.of(12, 45));
            w5.setNotes("Lunch break workout.");
            w5.setExerciseSets(new ArrayList<>());
            if (squat != null) {
                w5.getExerciseSets().add(createSet(w5, squat, 1, new BigDecimal("185.0"), 8, null));
                w5.getExerciseSets().add(createSet(w5, squat, 2, new BigDecimal("185.0"), 8, null));
            }
            if (bench != null) {
                w5.getExerciseSets().add(createSet(w5, bench, 1, new BigDecimal("155.0"), 8, null));
                w5.getExerciseSets().add(createSet(w5, bench, 2, new BigDecimal("155.0"), 8, null));
            }
            if (pullup != null) {
                w5.getExerciseSets().add(createSet(w5, pullup, 1, new BigDecimal("0.0"), 5, null));
                w5.getExerciseSets().add(createSet(w5, pullup, 2, new BigDecimal("0.0"), 5, null));
            }
            sampleWorkouts.add(w5);

            // Workout 6: Long Cycle
            Workout w6 = new Workout();
            w6.setUser(adminUser);
            w6.setTitle("Weekend Cycle");
            w6.setWorkoutDate(LocalDate.now().minusDays(2));
            w6.setTimeEnter(LocalTime.of(9, 0));
            w6.setTimeLeave(LocalTime.of(10, 30));
            w6.setNotes("Nice weather for a ride.");
            w6.setCardioSessions(new ArrayList<>());
            if (cycling != null) {
                w6.getCardioSessions().add(createCardio(w6, cycling, 90, new BigDecimal("25.0"), "Outdoor bike ride"));
            }
            sampleWorkouts.add(w6);

            // Workout 7: Heavy Single
            Workout w7 = new Workout();
            w7.setUser(adminUser);
            w7.setTitle("Heavy Singles Day");
            w7.setWorkoutDate(LocalDate.now().minusDays(1));
            w7.setTimeEnter(LocalTime.of(17, 0));
            w7.setTimeLeave(LocalTime.of(17, 45));
            w7.setNotes("Testing 1 rep max.");
            w7.setExerciseSets(new ArrayList<>());
            if (deadlift != null) {
                w7.getExerciseSets().add(createSet(w7, deadlift, 1, new BigDecimal("135.0"), 5, "Warm-up"));
                w7.getExerciseSets().add(createSet(w7, deadlift, 2, new BigDecimal("225.0"), 3, null));
                w7.getExerciseSets().add(createSet(w7, deadlift, 3, new BigDecimal("315.0"), 1, null));
                w7.getExerciseSets().add(createSet(w7, deadlift, 4, new BigDecimal("335.0"), 1, "New PR!"));
            }
            sampleWorkouts.add(w7);


            // Workout 8
            Workout w8 = new Workout();
            w8.setUser(adminUser);
            w8.setTitle("Volume Day");
            w8.setWorkoutDate(LocalDate.now().minusDays(8));
            w8.setTimeEnter(LocalTime.of(16, 0));
            w8.setTimeLeave(LocalTime.of(17, 0));
            w8.setNotes("Focus on form.");
            w8.setExerciseSets(new ArrayList<>());
            if (deadlift != null) {
                w8.getExerciseSets().add(createSet(w8, deadlift, 1, new BigDecimal("135.0"), 5, "Warm-up"));
                w8.getExerciseSets().add(createSet(w8, deadlift, 2, new BigDecimal("225.0"), 5, null));
                w8.getExerciseSets().add(createSet(w8, deadlift, 3, new BigDecimal("275.0"), 5, null));
                w8.getExerciseSets().add(createSet(w8, deadlift, 4, new BigDecimal("275.0"), 5, null));
                w8.getExerciseSets().add(createSet(w8, deadlift, 5, new BigDecimal("275.0"), 5, "Felt good"));
            }
            sampleWorkouts.add(w8);

// Workout 9
            Workout w9 = new Workout();
            w9.setUser(adminUser);
            w9.setTitle("Light Technique");
            w9.setWorkoutDate(LocalDate.now().minusDays(9));
            w9.setTimeEnter(LocalTime.of(18, 0));
            w9.setTimeLeave(LocalTime.of(18, 45));
            w9.setNotes("Speed pulls.");
            w9.setExerciseSets(new ArrayList<>());
            if (deadlift != null) {
                w9.getExerciseSets().add(createSet(w9, deadlift, 1, new BigDecimal("135.0"), 5, "Warm-up"));
                w9.getExerciseSets().add(createSet(w9, deadlift, 2, new BigDecimal("185.0"), 3, null));
                w9.getExerciseSets().add(createSet(w9, deadlift, 3, new BigDecimal("185.0"), 3, null));
                w9.getExerciseSets().add(createSet(w9, deadlift, 4, new BigDecimal("185.0"), 3, null));
                w9.getExerciseSets().add(createSet(w9, deadlift, 5, new BigDecimal("185.0"), 3, null));
                w9.getExerciseSets().add(createSet(w9, deadlift, 6, new BigDecimal("185.0"), 3, null));
            }
            sampleWorkouts.add(w9);

// Workout 10
            Workout w10 = new Workout();
            w10.setUser(adminUser);
            w10.setTitle("Pyramid Sets");
            w10.setWorkoutDate(LocalDate.now().minusDays(10));
            w10.setTimeEnter(LocalTime.of(17, 30));
            w10.setTimeLeave(LocalTime.of(18, 30));
            w10.setNotes("Working up and back down.");
            w10.setExerciseSets(new ArrayList<>());
            if (deadlift != null) {
                w10.getExerciseSets().add(createSet(w10, deadlift, 1, new BigDecimal("135.0"), 5, "Warm-up"));
                w10.getExerciseSets().add(createSet(w10, deadlift, 2, new BigDecimal("225.0"), 5, null));
                w10.getExerciseSets().add(createSet(w10, deadlift, 3, new BigDecimal("275.0"), 3, null));
                w10.getExerciseSets().add(createSet(w10, deadlift, 4, new BigDecimal("300.0"), 2, null));
                w10.getExerciseSets().add(createSet(w10, deadlift, 5, new BigDecimal("275.0"), 3, null));
                w10.getExerciseSets().add(createSet(w10, deadlift, 6, new BigDecimal("225.0"), 5, null));
            }
            sampleWorkouts.add(w10);

// Workout 11
            Workout w11 = new Workout();
            w11.setUser(adminUser);
            w11.setTitle("Heavy Triples");
            w11.setWorkoutDate(LocalDate.now().minusDays(11));
            w11.setTimeEnter(LocalTime.of(16, 15));
            w11.setTimeLeave(LocalTime.of(17, 0));
            w11.setNotes("Building strength.");
            w11.setExerciseSets(new ArrayList<>());
            if (deadlift != null) {
                w11.getExerciseSets().add(createSet(w11, deadlift, 1, new BigDecimal("135.0"), 5, "Warm-up"));
                w11.getExerciseSets().add(createSet(w11, deadlift, 2, new BigDecimal("225.0"), 3, null));
                w11.getExerciseSets().add(createSet(w11, deadlift, 3, new BigDecimal("295.0"), 3, null));
                w11.getExerciseSets().add(createSet(w11, deadlift, 4, new BigDecimal("295.0"), 3, null));
                w11.getExerciseSets().add(createSet(w11, deadlift, 5, new BigDecimal("295.0"), 3, "Tough last set"));
            }
            sampleWorkouts.add(w11);

// Workout 12
            Workout w12 = new Workout();
            w12.setUser(adminUser);
            w12.setTitle("Paused Deadlifts");
            w12.setWorkoutDate(LocalDate.now().minusDays(12));
            w12.setTimeEnter(LocalTime.of(17, 0));
            w12.setTimeLeave(LocalTime.of(17, 45));
            w12.setNotes("Pausing below the knee.");
            w12.setExerciseSets(new ArrayList<>());
            if (deadlift != null) {
                w12.getExerciseSets().add(createSet(w12, deadlift, 1, new BigDecimal("135.0"), 5, "Warm-up"));
                w12.getExerciseSets().add(createSet(w12, deadlift, 2, new BigDecimal("205.0"), 4, null));
                w12.getExerciseSets().add(createSet(w12, deadlift, 3, new BigDecimal("205.0"), 4, null));
                w12.getExerciseSets().add(createSet(w12, deadlift, 4, new BigDecimal("205.0"), 4, null));
            }
            sampleWorkouts.add(w12);

// Workout 13
            Workout w13 = new Workout();
            w13.setUser(adminUser);
            w13.setTitle("AMRAP Day");
            w13.setWorkoutDate(LocalDate.now().minusDays(13));
            w13.setTimeEnter(LocalTime.of(18, 0));
            w13.setTimeLeave(LocalTime.of(18, 45));
            w13.setNotes("As many reps as possible on last set.");
            w13.setExerciseSets(new ArrayList<>());
            if (deadlift != null) {
                w13.getExerciseSets().add(createSet(w13, deadlift, 1, new BigDecimal("135.0"), 5, "Warm-up"));
                w13.getExerciseSets().add(createSet(w13, deadlift, 2, new BigDecimal("225.0"), 5, null));
                w13.getExerciseSets().add(createSet(w13, deadlift, 3, new BigDecimal("275.0"), 3, null));
                w13.getExerciseSets().add(createSet(w13, deadlift, 4, new BigDecimal("305.0"), 8, "AMRAP set, felt strong"));
            }
            sampleWorkouts.add(w13);

            // Save all workouts
            workoutRepo.saveAll(sampleWorkouts);
            log.info("Created 13 sample workouts.");
        }

    }

    // --- Helper method to create ExerciseSet ---
    private ExerciseSet createSet(Workout workout, ExerciseList exerciseList, int setNumber,
                                  BigDecimal weight, int reps, String notes) {
        ExerciseSet set = new ExerciseSet();
        set.setWorkout(workout);
        set.setExerciseList(exerciseList);
        set.setSetNumber(setNumber);
        set.setWeight(weight);
        set.setReps(reps);
        set.setNotes(notes);
        return set;
    }

    // --- Helper method to create CardioSession ---
    private CardioSession createCardio(Workout workout, CardioList cardioList, int duration,
                                       BigDecimal distance, String notes) {
        CardioSession session = new CardioSession();
        session.setWorkout(workout);
        session.setCardioList(cardioList);
        session.setDurationMinutes(duration);
        session.setDistance(distance);
        session.setNotes(notes);
        return session;
    }
}