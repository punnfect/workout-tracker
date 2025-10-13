package com.github.punnfect.workout_tracker.services;

import com.github.punnfect.workout_tracker.entities.ExerciseList;
import com.github.punnfect.workout_tracker.repository.ExerciseListRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExerciseService {

    private final ExerciseListRepo exerciseListRepo;

    public ExerciseService(ExerciseListRepo exerciseListRepo) {
        this.exerciseListRepo = exerciseListRepo;
    }

    //returns all exercise options in db
    @Transactional(readOnly = true)
    public List<ExerciseList> getAllExercises() { return exerciseListRepo.findAll(); }
}