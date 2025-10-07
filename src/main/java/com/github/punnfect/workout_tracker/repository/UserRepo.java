package com.github.punnfect.workout_tracker.repository;

import com.github.punnfect.workout_tracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    //Finds user by username, used for connecting a user to almost everything
    Optional<User> findByUsername(String username);
}
