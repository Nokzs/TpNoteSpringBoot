package com.example.demo.dto.book;

import jakarta.validation.constraints.NotBlank;

public record CreateBookDto(@NotBlank String name) {
}
