package com.github.punnfect.workout_tracker.repository;

import com.github.punnfect.workout_tracker.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutRepo extends JpaRepository<Workout, Long> {
}
