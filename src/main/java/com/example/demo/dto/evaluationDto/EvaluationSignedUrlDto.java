package com.example.demo.dto.evaluationDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

public record EvaluationSignedUrlDto(
        @Schema(description = "clé associée aux photos doit être renvoyé lors de la creation d'une évaluation afin d'y associer les photos")
        ArrayList<String> keys,
        @Schema(description = "urls signé pour upload les images")
        List<String> urls
) {
}
