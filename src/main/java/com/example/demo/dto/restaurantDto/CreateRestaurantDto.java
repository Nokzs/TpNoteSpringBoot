package com.example.demo.dto.restaurantDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRestaurantDto(
        @Schema(description = "nom du restaurant", maxLength = 90)
        @NotBlank() @Size(max=90) String name,
        @Schema(description = "l'adresse du restaurant", maxLength = 255)
        @NotBlank() @Size(max= 255) String address,
        @Schema(description = "clé qui permet d'associer son image au restaurant, les images doit être upload avant de crée le restaurant")
        String url) {
}
