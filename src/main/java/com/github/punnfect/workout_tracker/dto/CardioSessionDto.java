package com.github.punnfect.workout_tracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CardioSessionDto {
    private Long cardioListId;
    private int durationMinutes;
    private BigDecimal distance;
}