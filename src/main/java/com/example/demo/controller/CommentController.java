package com.example.demo.controller;

import com.example.demo.dto.comment.CommentDto;
import com.example.demo.service.CommentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comments")
    public CommentDto getComments(){
        return commentService.getComments();
    }
}
