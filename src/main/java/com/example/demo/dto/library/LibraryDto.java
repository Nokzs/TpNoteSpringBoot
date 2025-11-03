package com.example.demo.dto.library;

import com.example.demo.entity.LibraryEntity;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

public record LibraryDto(@Min(3) Integer id, @Length(max = 10) String name) {

    public static LibraryDto buildFromEntity(LibraryEntity libraryEntity) {
        return new LibraryDto(libraryEntity.getId().intValue(), libraryEntity.getName());
    }

}
