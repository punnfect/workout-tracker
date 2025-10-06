package com.github.punnfect.workout_tracker.repository;

import com.github.punnfect.workout_tracker.entities.ExerciseList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseListRepo extends JpaRepository<ExerciseList, Long> {
}
