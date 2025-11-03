package com.example.demo.controller;

import com.example.demo.dto.library.LibraryDto;
import com.example.demo.service.LibraryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
@Slf4j
public class LibraryController {

    private final LibraryService libraryService;
    @Value("datasource-url")
    private String datasource;

    @Value("${PATH}")
    private String path;

    public LibraryController(LibraryService libraryService ) {
        this.libraryService = libraryService;

    }

    @GetMapping
    public List<LibraryDto> getLibraries() {
        return this.libraryService.getLibraries()
                .stream()
                .map(LibraryDto::buildFromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public LibraryDto getLibraryById(@PathVariable("id") Integer id) {
        return LibraryDto.buildFromEntity(this.libraryService.getLibraryById(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    public void createLibrary(@Valid @RequestBody LibraryDto libraryDto, Authentication principal) {
        this.libraryService.createLibrary(libraryDto);
    }

}
