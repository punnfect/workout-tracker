package com.github.punnfect.workout_tracker.controller;

import com.github.punnfect.workout_tracker.dto.ExerciseProgressDto;
import com.github.punnfect.workout_tracker.entities.ExerciseList;
import com.github.punnfect.workout_tracker.services.ExerciseService;
import com.github.punnfect.workout_tracker.services.ProgressService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ProgressController {

    private final ProgressService progressService;
    private final ExerciseService exerciseService;

    public ProgressController(ProgressService progressService, ExerciseService exerciseService) {
        this.progressService = progressService;
        this.exerciseService = exerciseService;
    }

    // progress tracking page
    @GetMapping("/progress")
    public String showProgressPage(
            @RequestParam(value = "exerciseId", required = false) Long exerciseId,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "timeRange", required = false) String timeRange,
            Model model) {

        // Get all exercises for the dropdown
        List<ExerciseList> allExercises = exerciseService.getAllExercises();
        model.addAttribute("allExercises", allExercises);

        // If an exercise is selected, fetch and display progress data
        if (exerciseId != null) {

            try {

                if (timeRange != null && !timeRange.isEmpty()) {
                    endDate = LocalDate.now();
                    startDate = calculateStartDateFromRange(timeRange, endDate);
                }

                // Fetch progress data
                ExerciseProgressDto progressData = progressService.getExerciseProgress(
                        exerciseId, startDate, endDate);

                model.addAttribute("progressData", progressData);
                model.addAttribute("selectedExerciseId", exerciseId);
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                model.addAttribute("selectedTimeRange", timeRange);

            } catch (Exception e) {
                model.addAttribute("error", "Failed to load progress data: " + e.getMessage());
            }
        }

        return "progress";
    }

    // helper for set date ranges
    private LocalDate calculateStartDateFromRange(String timeRange, LocalDate endDate) {
        return switch (timeRange) {
            case "1month" -> endDate.minusMonths(1);
            case "3months" -> endDate.minusMonths(3);
            case "6months" -> endDate.minusMonths(6);
            case "9months" -> endDate.minusMonths(9);
            case "1year" -> endDate.minusYears(1);
            case "alltime" -> null;
            default -> endDate.minusMonths(3);
        };
    }
}