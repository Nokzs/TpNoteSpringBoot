package com.example.demo.entity;

import com.example.demo.dto.book.BookDto;
import com.example.demo.dto.book.UpdateBookDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity(name = "book")
@Data
public class BookEntity {
    @Column(name = "isbn")
    @Id
    private String isbn;


    @Column(name = "name")
    private String name = null;

    @ManyToMany(mappedBy = "book")
    private List<LibraryEntity> libraryEntities;
    @Column
    private String jacquette;
    public BookEntity(BookDto bookDto) {
        this.isbn = bookDto.isbn();
        this.name = bookDto.name();

    }

    public BookEntity() {

    }




    public void update(UpdateBookDto updateBookDto){
            this.name = name;
    }
}
