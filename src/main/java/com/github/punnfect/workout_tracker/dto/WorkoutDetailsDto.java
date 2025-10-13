package com.github.punnfect.workout_tracker.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class WorkoutDetailsDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime timeEnter;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime timeLeave;

    private String workoutNotes;

    private List<ExerciseSetDto> exerciseSets = new ArrayList<>();
    private List<CardioSessionDto> cardioSessions = new ArrayList<>();
}