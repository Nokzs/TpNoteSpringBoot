package com.example.demo.service;

import com.example.demo.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    public final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
}
