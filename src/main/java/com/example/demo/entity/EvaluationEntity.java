package com.example.demo.entity;

import com.example.demo.dto.evaluationDto.CreateEvaluationDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "id de l'evaluation")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="evaluatorName",length = 50)
    @Schema(description = "nom de l'évaluateur")
    @Size(max=255)
    private String evaluatorName;

    @Schema(description = "commentaire de l'évaluation")
    @Column(name="Content")
    private String content;

    @Min(0)
    @Max(3)
    @Column(name = "note")
    @Schema(description = "note de l'évaluation")
    private int note;

    @ManyToOne()
    @JoinColumn(name = "restaurant_id", nullable = false)
    @Schema(description = "restaurant associée à l'évaluation")
    private RestaurantEntity restaurant;

    @Column(name="keys")
    @Schema(description = "clé associée aux photo")
    List<String> keys;

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

    public EvaluationEntity() {

    }
}