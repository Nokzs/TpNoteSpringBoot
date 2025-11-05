package com.example.demo.dto.restaurantDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantDto(
        @Schema(description = "id du restaurant")
        @Positive
        Long id,
        @Schema(description = "nom du restaurant")
        @Size(max=90)
        String name,
        @Schema(description = "address du restaurant")
        @Size(max = 255) String address) {
}
