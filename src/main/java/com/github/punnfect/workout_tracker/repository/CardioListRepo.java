package com.github.punnfect.workout_tracker.repository;

import com.github.punnfect.workout_tracker.entities.CardioList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardioListRepo extends JpaRepository<CardioList, Long> {
}
