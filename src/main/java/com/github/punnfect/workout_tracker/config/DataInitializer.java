package com.github.punnfect.workout_tracker.config;

import com.github.punnfect.workout_tracker.entities.User;
import com.github.punnfect.workout_tracker.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//Used for initializing a username/password. Can change later
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
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
    }
}