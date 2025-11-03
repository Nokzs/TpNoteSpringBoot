package com.example.demo.dto.restaurantDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateRestaurantDto(@NotBlank() @Size(max=90) String name, @NotBlank() @Size(max= 255) String address,String url) {
}
