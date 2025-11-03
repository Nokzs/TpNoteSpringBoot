package com.example.demo.dto.comment;

import com.example.demo.dto.book.BookDto;
import com.example.demo.entity.BookEntity;
import com.example.demo.entity.CommentEntity;

public record CommentDto(String id,String name, String content, BookDto books) {
   public static CommentDto BuildfromComment(CommentEntity comment, BookEntity book){
        return new CommentDto(comment.getAuthor(),comment.getContent(),comment.getContent(),BookDto.buildFromBook(book));
    }
}
