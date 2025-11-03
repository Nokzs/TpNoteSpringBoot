package com.example.demo.service;

import com.example.demo.dto.book.BookDto;
import com.example.demo.dto.book.CreateBookDto;
import com.example.demo.dto.book.UpdateBookDto;
import com.example.demo.entity.BookEntity;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private BookRepository bookRepository = null;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void createBook(CreateBookDto bookDto) {

        var isbn = this.generateIsbn();
        var newBook = new BookEntity(new BookDto(isbn,bookDto.name()));
        this.bookRepository.save(newBook);
    }

    private String generateIsbn() {
        var value = Math.random() * 1000000000000L;
        return String.format("%013.0f", value);
    }

    public BookEntity getBookByIsbn(String isbn){
        Optional<BookEntity> byId = this.bookRepository.findById(isbn);
        if(byId.isPresent()){
            return byId.get();
        }
        return null;
    }

    public void update(String isbn, UpdateBookDto updateBookDto) {
        Optional<BookEntity> book = this.bookRepository.findById(isbn);
        book.ifPresent(this.bookRepository::save);
    }
    public List<BookEntity> getBooks(){
        return bookRepository.findAll();
    }
}
