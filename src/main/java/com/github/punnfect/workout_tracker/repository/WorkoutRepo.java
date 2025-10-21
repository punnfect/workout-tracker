package com.github.punnfect.workout_tracker.repository;

import com.github.punnfect.workout_tracker.entities.User;
import com.github.punnfect.workout_tracker.entities.Workout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkoutRepo extends JpaRepository<Workout, Long> {

    //Will list a users workouts from new to old
    List<Workout> findByUserOrderByWorkoutDateDesc(User user);

    @Query("SELECT DISTINCT w FROM Workout w " +
            "LEFT JOIN FETCH w.exerciseSets es " +
            "LEFT JOIN FETCH es.exerciseList " +
            "WHERE w.id = :id")
    Optional<Workout> findByIdWithExerciseSets(@Param("id") Long id);

    @Query("SELECT DISTINCT w FROM Workout w " +
            "LEFT JOIN FETCH w.cardioSessions cs " +
            "LEFT JOIN FETCH cs.cardioList " +
            "WHERE w.id = :id")
    Optional<Workout> findByIdWithCardioSessions(@Param("id") Long id);
}
