package com.example.demo.dto.restaurantDto;

import com.example.demo.dto.evaluationDto.EvaluationDto;
import com.example.demo.entity.RestaurantEntity;
import com.google.common.util.concurrent.AtomicDouble;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public record RestaurantDto(
        @Schema(description = "id du restaurant")
        Long id,
        @Schema(description = "nom du restaurant")
        @Size(max = 90) @NotBlank()
        String name,
        @Schema(description = "adresse du restaurant" )
        String address,
        @Schema(description = "moyenne des évaluations, -1 si aucune évaluations")
        double moyenne,
        @Schema(description = "liste des evaluations associées")
        List<EvaluationDto> evaluation,
        @Schema(description = "url de l'image du restaurant")
        String url
) {


}
