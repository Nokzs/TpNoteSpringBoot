package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.UUID;


@Setter
@Getter
@Data
@Entity(name="comment")
public class CommentEntity {
    @Id
    @Column(name="id")
    @UUID
    private Long id;

    @NotBlank
    @Column(name="author")
    private String author;

    @Column(name="content")
    private String content;

}
