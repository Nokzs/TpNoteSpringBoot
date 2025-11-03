package com.example.demo.dto.book;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UpdateBookDto(@NotBlank @Length(max=100) String name) {
}

