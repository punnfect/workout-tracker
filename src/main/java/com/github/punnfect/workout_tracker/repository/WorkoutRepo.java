package com.github.punnfect.workout_tracker.repository;

import com.github.punnfect.workout_tracker.entities.User;
import com.github.punnfect.workout_tracker.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutRepo extends JpaRepository<Workout, Long> {

    //Will list a users workouts from new to old
    List<Workout> findByUserOrderByWorkoutDateDesc(User user);

}
