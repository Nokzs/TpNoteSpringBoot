package com.example.demo.entity;

import com.example.demo.dto.evaluationDto.CreateEvaluationDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

    public EvaluationEntity(CreateEvaluationDto createEvaluationDto,RestaurantEntity r,String name) {
        List<EvaluationEntity> evaluationList = r.getEvaluation();
        this.setEvaluatorName(name);
        this.setNote(createEvaluationDto.note());
        this.setContent(createEvaluationDto.content());
        evaluationList.add(this);
        this.setRestaurant(r);
        this.setKeys(createEvaluationDto.keys());
        r.setEvaluation(evaluationList);
    }
}