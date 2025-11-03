package com.example.demo.service;

import com.example.demo.dto.library.LibraryDto;
import com.example.demo.entity.LibraryEntity;
import com.example.demo.repository.LibraryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class LibraryService {

    private final LibraryRepository libraryRepository;

    public LibraryService(LibraryRepository libraryRepository) {
        this.libraryRepository = libraryRepository;
    }

    public List<LibraryEntity> getLibraries() {
        return this.libraryRepository.findAll();
    }

    public LibraryEntity getLibraryById(Integer id) {
        Optional<LibraryEntity> library = libraryRepository.findById(Long.valueOf(id));

        return library.orElse(null);
    }

    public void createLibrary(LibraryDto libraryDto) {
        LibraryEntity library = new LibraryEntity();
        library.extractFromDto(libraryDto);
        this.libraryRepository.save(library);
    }

}
