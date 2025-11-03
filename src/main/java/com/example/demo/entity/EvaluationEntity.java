package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;

@Entity(name = "evaluation")
@Data

public class EvaluationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="evaluatorName",length = 50)
    @Size(max=255)
    private String evaluatorName;

    @Column(name="Content",length = 255)
    private String content;

    @Min(0)
    @Max(3)
    @Column(name = "note")
    private int note;

    @ManyToOne()
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;

    @Column(name="keys")
    ArrayList<String> keys;
}