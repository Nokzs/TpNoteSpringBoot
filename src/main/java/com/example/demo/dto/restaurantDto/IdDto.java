package com.example.demo.dto.restaurantDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record IdDto(@NotBlank() @Positive() long id) {
}
