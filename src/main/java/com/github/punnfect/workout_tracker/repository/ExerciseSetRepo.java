package com.github.punnfect.workout_tracker.repository;

import com.github.punnfect.workout_tracker.entities.ExerciseSet;
import com.github.punnfect.workout_tracker.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExerciseSetRepo extends JpaRepository<ExerciseSet, Long> {

    // finds all exercise sets for a specific exercise within startDate - endDate inclusive
    @Query("SELECT es FROM ExerciseSet es " +
            "JOIN FETCH es.exerciseList " +
            "JOIN FETCH es.workout w " +
            "WHERE es.exerciseList.id = :exerciseListId " +
            "AND w.user = :user " +
            "AND w.workoutDate BETWEEN :startDate AND :endDate " +
            "ORDER BY w.workoutDate ASC, es.setNumber ASC")
    List<ExerciseSet> findExerciseProgressByDateRange(
            @Param("exerciseListId") Long exerciseListId,
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // finds all exercise sets for a specific exercise over all time
    @Query("SELECT es FROM ExerciseSet es " +
            "JOIN FETCH es.exerciseList " +
            "JOIN FETCH es.workout w " +
            "WHERE es.exerciseList.id = :exerciseListId " +
            "AND w.user = :user " +
            "ORDER BY w.workoutDate ASC, es.setNumber ASC")
    List<ExerciseSet> findAllExerciseProgress(
            @Param("exerciseListId") Long exerciseListId,
            @Param("user") User user
    );
}