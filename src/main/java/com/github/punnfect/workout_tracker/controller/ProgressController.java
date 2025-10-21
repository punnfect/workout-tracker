package com.github.punnfect.workout_tracker.controller;

import com.github.punnfect.workout_tracker.services.ProgressService;
import org.springframework.stereotype.Controller;

@Controller
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }
}
