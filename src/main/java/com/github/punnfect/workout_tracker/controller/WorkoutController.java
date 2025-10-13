package com.github.punnfect.workout_tracker.controller;

import com.github.punnfect.workout_tracker.dto.WorkoutSummaryDto;
import com.github.punnfect.workout_tracker.entities.CardioList;
import com.github.punnfect.workout_tracker.entities.ExerciseList;
import com.github.punnfect.workout_tracker.entities.Workout;
import com.github.punnfect.workout_tracker.services.CardioService;
import com.github.punnfect.workout_tracker.services.ExerciseService;
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
    private final ExerciseService exerciseService;
    private final CardioService cardioService;

    public WorkoutController(WorkoutService workoutService, ExerciseService exerciseService, CardioService cardioService) {
        this.workoutService = workoutService;
        this.exerciseService = exerciseService;
        this.cardioService = cardioService;
    }

    //displays home page with list of all users workout summaries
    @GetMapping("/")
    public String home(Model model) {
        List<WorkoutSummaryDto> workouts = workoutService.getWorkoutHistoryForCurrentUser();
        model.addAttribute("workouts", workouts);
        return "home";
    }


    //Creates base for workout and redirects to add all workout info page
    @PostMapping("/workouts/create")
    public String createWorkout(@RequestParam("workoutDate") LocalDate workoutDate,
                                @RequestParam("title") String title) {
        Workout newWorkout = workoutService.createNewWorkout(workoutDate, title);

        return "redirect:/workouts/" + newWorkout.getId() + "/add";
    }

    //returns addWorkout page for adding entire workout, gives access to exercises/cardio in db
    @GetMapping("/workouts/{id}/add")
    public String showAddWorkoutDetailsForm(@PathVariable("id") Long id, Model model) {
        Optional<Workout> workoutOpt = workoutService.getWorkoutDetails(id);
        List<ExerciseList> allExercises = exerciseService.getAllExercises();
        List<CardioList> allCardio = cardioService.getAllCardioActivities();

        if (workoutOpt.isPresent()) {
            model.addAttribute("workout", workoutOpt.get());
            model.addAttribute("allExercises", allExercises);
            model.addAttribute("allCardio", allCardio);
            return "addWorkout";
        } else {
            return "redirect:/";
        }
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