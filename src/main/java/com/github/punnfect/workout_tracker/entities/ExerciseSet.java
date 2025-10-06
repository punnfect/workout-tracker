package com.github.punnfect.workout_tracker.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "exercise_sets", uniqueConstraints = {@UniqueConstraint(columnNames = {"workout_id", "exercise_list_id", "set_number"})})
public class ExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    @ManyToOne
    @JoinColumn(name = "exercise_list_id", nullable = false)
    private ExerciseList exerciseList;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(name = "weight", precision = 7, scale = 2)
    private BigDecimal weight;

    @Column(name = "reps")
    private Integer reps;

    @Column(name = "notes", length = 255)
    private String notes;

}
