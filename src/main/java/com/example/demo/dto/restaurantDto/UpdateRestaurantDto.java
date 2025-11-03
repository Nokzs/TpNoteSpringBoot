package com.example.demo.dto.restaurantDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantDto(Long id, @NotBlank() @Size(max=90) String name, @NotBlank() @Size(max = 255) String address) {
}
