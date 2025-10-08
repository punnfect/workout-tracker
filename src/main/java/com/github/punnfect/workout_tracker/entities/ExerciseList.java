package com.github.punnfect.workout_tracker.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "exercise_list")
public class ExerciseList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exercise_list_id")
    private Long id;

    @Column(name = "name", nullable = false,unique = true, length = 100)
    private  String name;

    @OneToMany(mappedBy = "exerciseList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseSet> exerciseSets;
}
