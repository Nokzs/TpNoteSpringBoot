package com.example.demo.dto.restaurantDto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RestaurantSignedUrlDto(
        @Schema(description = "url permettant l'upload vers le bucket")
        String url,
        @Schema(description = "clé à renvoyer lors de la création afin d'associer les images aux évaluations")
        String key) {
}
