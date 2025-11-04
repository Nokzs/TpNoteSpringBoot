package com.example.demo.dto.evaluationDto;

import com.example.demo.entity.EvaluationEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;


public record EvaluationDto(
        @Schema(description = "id de l'évaluation")
        Long id,
        @Schema(description = "nom de l'evaluateur",maxLength = 50)
        @NotBlank() @Max(50) String name,
        @Schema(description = "note de l'evaluation compris entre 0 et 3")
        @Min(0) @Max(3) int note,

        @Schema(description = "commentaire de l'utilisateur",maxLength = 255)
        @NotBlank @Size(max = 255)
        String content,

        @Schema(description = "id du restaurant")
        @Positive @NotBlank()
        long restaurantId,

        @Schema(description = "liste des urls des images")
        List<String> urls
        ) {
    public static EvaluationDto buildFromEntity(EvaluationEntity entity, List<String> urls) {

       return new EvaluationDto(
               entity.getId(),
               entity.getEvaluatorName(),
               entity.getNote(),
               entity.getContent(),
               entity.getRestaurant().getId(),
               urls);
    }

}
