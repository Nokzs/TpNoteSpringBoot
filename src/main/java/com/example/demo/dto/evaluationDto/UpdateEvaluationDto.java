package com.example.demo.dto.evaluationDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;

public record UpdateEvaluationDto(
        @Schema(description = "commentaire de l'utilisateur")
        @Size(max = 255) String content,

        @Schema(description = "note pouvant être entre 0 et 3")
        @Max(3) @Min(0)  Integer note,

        @Schema(description = "clé qui ont permit l'upload des fichiers")
        ArrayList<String> keys
) {
}
