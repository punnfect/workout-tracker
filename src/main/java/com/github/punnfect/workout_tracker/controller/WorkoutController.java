package com.github.punnfect.workout_tracker.controller;

import com.github.punnfect.workout_tracker.dto.WorkoutCreateDto;
import com.github.punnfect.workout_tracker.dto.WorkoutDetailsDto;
import com.github.punnfect.workout_tracker.dto.WorkoutSummaryDto;
import com.github.punnfect.workout_tracker.entities.CardioList;
import com.github.punnfect.workout_tracker.entities.ExerciseList;
import com.github.punnfect.workout_tracker.entities.Workout;
import com.github.punnfect.workout_tracker.services.CardioService;
import com.github.punnfect.workout_tracker.services.ExerciseService;
import com.github.punnfect.workout_tracker.services.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class WorkoutController {

    private final WorkoutService workoutService;
    private final ExerciseService exerciseService;
    private final CardioService cardioService;

    public WorkoutController(WorkoutService workoutService, ExerciseService exerciseService, CardioService cardioService) {
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
        this.cardioService = cardioService;
    }

    // Display home page with list of all users workout summaries
    @GetMapping("/")
    public String home(Model model) {
        List<WorkoutSummaryDto> workouts = workoutService.getWorkoutHistoryForCurrentUser();
        model.addAttribute("workouts", workouts);

        // Add empty DTO for the form
        if (!model.containsAttribute("workoutCreateDto")) {
            WorkoutCreateDto dto = new WorkoutCreateDto();
            dto.setWorkoutDate(LocalDate.now()); // Set default to today
            model.addAttribute("workoutCreateDto", dto);
        }

        return "home";
    }

    // Creates base for workout and redirects to add all workout info page
    @PostMapping("/workouts/create")
    public String createWorkout(@Valid @ModelAttribute("workoutCreateDto") WorkoutCreateDto createDto,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            // Add error messages to flash attributes
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.workoutCreateDto", bindingResult);
            redirectAttributes.addFlashAttribute("workoutCreateDto", createDto);
            return "redirect:/";
        }

        // Additional custom validation
        if (createDto.getWorkoutDate().isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "Workout date cannot be in the future");
            redirectAttributes.addFlashAttribute("workoutCreateDto", createDto);
            return "redirect:/";
        }

        try {
            Workout newWorkout = workoutService.createNewWorkout(
                    createDto.getWorkoutDate(),
                    createDto.getTitle()
            );

            redirectAttributes.addFlashAttribute("success", "Workout created successfully!");
            return "redirect:/workouts/" + newWorkout.getId() + "/add";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create workout: " + e.getMessage());
            redirectAttributes.addFlashAttribute("workoutCreateDto", createDto);
            return "redirect:/";
        }
    }

    // Deletes a workout and redirects to home
    @PostMapping("/workouts/{id}/delete")
    public String deleteWorkout(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            workoutService.deleteWorkout(id);
            redirectAttributes.addFlashAttribute("success", "Workout deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete workout: " + e.getMessage());
        }
        return "redirect:/";
    }

    // Adds everything from addWorkout page to the associated workout
    @PostMapping("/workouts/{id}/save")
    public String saveWorkoutDetails(@PathVariable("id") Long workoutId,
                                     @Valid @ModelAttribute("detailsDto") WorkoutDetailsDto detailsDto,
                                     BindingResult bindingResult,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {

        // Any errors on backend will reload form with error messages
        if (bindingResult.hasErrors()) {
            Optional<Workout> workoutOpt = workoutService.getWorkoutDetails(workoutId);
            if (workoutOpt.isPresent()) {
                model.addAttribute("workout", workoutOpt.get());
                model.addAttribute("allExercises", exerciseService.getAllExercises());
                model.addAttribute("allCardio", cardioService.getAllCardioActivities());
                model.addAttribute("validationErrors", bindingResult.getAllErrors());
                return "addWorkout";
            } else {
                redirectAttributes.addFlashAttribute("error", "Workout not found");
                return "redirect:/";
            }
        }

        // Additional validation for time fields
        if (detailsDto.getTimeEnter() != null && detailsDto.getTimeLeave() != null) {
            if (detailsDto.getTimeLeave().isBefore(detailsDto.getTimeEnter())) {
                Optional<Workout> workoutOpt = workoutService.getWorkoutDetails(workoutId);
                if (workoutOpt.isPresent()) {
                    model.addAttribute("workout", workoutOpt.get());
                    model.addAttribute("allExercises", exerciseService.getAllExercises());
                    model.addAttribute("allCardio", cardioService.getAllCardioActivities());
                    model.addAttribute("error", "Leave time cannot be before enter time");
                    return "addWorkout";
                }
            }
        }

        try {
            workoutService.saveWorkoutDetails(
                    workoutId,
                    detailsDto.getWorkoutNotes(),
                    detailsDto.getTimeEnter(),
                    detailsDto.getTimeLeave(),
                    detailsDto.getExerciseSets(),
                    detailsDto.getCardioSessions()
            );
            redirectAttributes.addFlashAttribute("success", "Workout details saved successfully!");
            return "redirect:/workout/" + workoutId;

        } catch (Exception e) {
            model.addAttribute("error", "Failed to save workout: " + e.getMessage());
            Optional<Workout> workoutOpt = workoutService.getWorkoutDetails(workoutId);
            if (workoutOpt.isPresent()) {
                model.addAttribute("workout", workoutOpt.get());
                model.addAttribute("allExercises", exerciseService.getAllExercises());
                model.addAttribute("allCardio", cardioService.getAllCardioActivities());
            }
            return "addWorkout";
        }
    }

    // Returns addWorkout page for adding entire workout
    @GetMapping("/workouts/{id}/add")
    public String showAddWorkoutDetailsForm(@PathVariable("id") Long id,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        Optional<Workout> workoutOpt = workoutService.getWorkoutDetails(id);

        if (workoutOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Workout not found");
            return "redirect:/";
        }

        List<ExerciseList> allExercises = exerciseService.getAllExercises();
        List<CardioList> allCardio = cardioService.getAllCardioActivities();

        model.addAttribute("workout", workoutOpt.get());
        model.addAttribute("allExercises", allExercises);
        model.addAttribute("allCardio", allCardio);

        if (!model.containsAttribute("detailsDto")) {
            model.addAttribute("detailsDto", new WorkoutDetailsDto());
        }

        return "addWorkout";
    }

    // Returns workout page for entire workout
    @GetMapping("/workout/{id}")
    public String viewWorkout(@PathVariable("id") Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        Optional<Workout> workoutOpt = workoutService.getWorkoutDetails(id);

        if (workoutOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Workout not found");
            return "redirect:/";
        }

        model.addAttribute("workout", workoutOpt.get());
        return "workout";
    }
}