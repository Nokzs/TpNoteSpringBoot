package com.example.demo.dto.evaluationDto;

import com.example.demo.entity.EvaluationEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record EvaluationDto(Long id, @NotBlank() @Max(50) String name, @Min(0) @Max(3) int note, String content, long restaurantId) {
    public static EvaluationDto buildFromEntity(EvaluationEntity entity) {
       return new EvaluationDto(entity.getId(),entity.getEvaluatorName(),entity.getNote(),entity.getContent(),entity.getRestaurant().getId());
    }

}
