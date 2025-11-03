package com.example.demo.entity;

import com.example.demo.dto.library.LibraryDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.List;

@Entity(name = "library")
@Data
public class LibraryEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany
    private List<BookEntity> book;

    public void extractFromDto(LibraryDto library){
        this.setId(Long.valueOf(library.id()));
        this.setName(library.name());
    }

}
