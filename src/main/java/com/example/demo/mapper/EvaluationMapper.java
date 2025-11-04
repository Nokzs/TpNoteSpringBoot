package com.example.demo.mapper;

import com.example.demo.dto.evaluationDto.EvaluationDto;
import com.example.demo.entity.EvaluationEntity;
import com.example.demo.utils.MinioService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluationMapper {
    private final MinioService minioService;
     EvaluationMapper(MinioService minioService) {
        this.minioService = minioService;
    }

    public EvaluationDto toDto(EvaluationEntity entity) {
        List<String> urls = new ArrayList<>();

        if (entity.getKeys() != null) {
            urls = entity.getKeys().stream().map(this.minioService::getPublicUrl).toList();
        }
        return new EvaluationDto(
                entity.getId(),
                entity.getEvaluatorName(),
                entity.getNote(),
                entity.getContent(),
                entity.getRestaurant().getId(),
                urls);
    }
}

