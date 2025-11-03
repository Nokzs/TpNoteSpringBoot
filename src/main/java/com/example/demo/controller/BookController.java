package com.example.demo.controller;

import com.example.demo.dto.book.BookDto;
import com.example.demo.dto.book.CreateBookDto;
import com.example.demo.dto.book.UpdateBookDto;
import com.example.demo.dto.storage.SignedUrlDto;
import com.example.demo.entity.BookEntity;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;
import com.example.demo.service.MinioService;
import io.minio.errors.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/book")
@Slf4j
public class BookController {
    private final MinioService minioService;
    private final BookService bookService;

    public BookController(MinioService minioService, BookService bookService) {
        this.minioService = minioService;
        this.bookService = bookService;
    }

    @PostMapping
    public void createBook(@Valid @RequestBody CreateBookDto bookDto) {
        this.bookService.createBook(bookDto);
    }

    @PutMapping("/{isbn}")
    public void updateBook(UpdateBookDto updateBookDto, @PathVariable("isbn") String isbn){
        this.bookService.update(isbn,updateBookDto);
    }

    @GetMapping("/{isbn}/jaquette")
    public SignedUrlDto createJaquette(@PathVariable("isbn") String isbn){
        try {

            String url = this.minioService.getSignedUrlCover(isbn);
            BookEntity bookByIsbn = this.bookService.getBookByIsbn(isbn);

            return new SignedUrlDto(url);
        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping()
    public List<BookDto> getBooks(){
        List<BookEntity> books = this.bookService.getBooks();
        return books.stream().map(BookDto::buildFromBook).toList();
    }

}
