package com.github.punnfect.workout_tracker.services;

import com.github.punnfect.workout_tracker.repository.ExerciseSetRepo;
import org.springframework.stereotype.Service;

@Service
public class ProgressService {

    private final ExerciseSetRepo exerciseSetRepo;

    public ProgressService(ExerciseSetRepo exerciseSetRepo) {
        this.exerciseSetRepo = exerciseSetRepo;

    }


}
