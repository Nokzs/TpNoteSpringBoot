package com.example.demo.dto.book;

import com.example.demo.entity.BookEntity;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.ISBN;

public record BookDto(@ISBN String isbn, @NotBlank String name) {
    public static BookDto buildFromBook(BookEntity book){
        return new BookDto(book.getIsbn(),book.getName());
    }
}
