package com.github.punnfect.workout_tracker.repository;

import com.github.punnfect.workout_tracker.entities.ExerciseSet;
import com.github.punnfect.workout_tracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseSetRepo extends JpaRepository<ExerciseSet, Long> {

    //finds all sets for a specific exercise by user, ordered by workout date ascending
    List<ExerciseSet> findByExerciseListIdAndWorkout_UserOrderByWorkout_WorkoutDateAsc(Long exerciseListId, User user);
}
