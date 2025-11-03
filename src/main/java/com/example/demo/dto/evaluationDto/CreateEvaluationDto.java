package com.example.demo.dto.evaluationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CreateEvaluationDto(
        @Schema(description = "commentaire de l'utilisateur")
        @Size(max = 255) String content,

        @Schema(description = "note pouvant être entre 0 et 3")
        @Max(3) @Min(0) int note,

        @Schema(description = "id du restaurant")
        @Positive long restaurantId) {
}
