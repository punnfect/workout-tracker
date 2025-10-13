package com.github.punnfect.workout_tracker.controller;

import com.github.punnfect.workout_tracker.dto.WorkoutSummaryDto;
import com.github.punnfect.workout_tracker.entities.Workout;
import com.github.punnfect.workout_tracker.services.WorkoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    //displays home page with list of all users workout summaries
    @GetMapping("/")
    public String home(Model model) {
        List<WorkoutSummaryDto> workouts = workoutService.getWorkoutHistoryForCurrentUser();
        model.addAttribute("workouts", workouts);
        return "home";
    }

    //displays addWorkout page for creating a new workout
    @GetMapping("/workouts/new")
    public String showAddWorkoutForm() {
        return "addWorkout";
    }

    //redirects to the workout page for the created workout
    @PostMapping("/workouts/create")
    public String createWorkout(@RequestParam("workoutDate") LocalDate workoutDate,
                                @RequestParam("title") String title) {
        Workout newWorkout = workoutService.createNewWorkout(workoutDate, title);
        return "redirect:/workout/" + newWorkout.getId();
    }

    //returns workout page for entire workout
    @GetMapping("/workout/{id}")
    public String viewWorkout(@PathVariable("id") Long id, Model model) {
        Optional<Workout> workoutOpt = workoutService.getWorkoutDetails(id);
        if (workoutOpt.isPresent()) {
            model.addAttribute("workout", workoutOpt.get());
            return "workout";
        } else {
            //will redirect home if page isn't found
            return "redirect:/";
        }
    }
}