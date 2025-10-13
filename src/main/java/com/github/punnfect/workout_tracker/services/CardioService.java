package com.github.punnfect.workout_tracker.services;

import com.github.punnfect.workout_tracker.entities.CardioList;
import com.github.punnfect.workout_tracker.repository.CardioListRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardioService {

    private final CardioListRepo cardioListRepo;

    public CardioService(CardioListRepo cardioListRepo) {
        this.cardioListRepo = cardioListRepo;
    }

    //Returns all db entered cardio activities
    @Transactional(readOnly = true)
    public List<CardioList> getAllCardioActivities() {
        return cardioListRepo.findAll();
    }
}