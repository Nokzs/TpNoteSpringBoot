package com.example.demo.mapper;


import com.example.demo.dto.evaluationDto.EvaluationDto;
import com.example.demo.dto.restaurantDto.RestaurantDto;
import com.example.demo.entity.RestaurantEntity;
import com.example.demo.utils.MinioService;
import com.google.common.util.concurrent.AtomicDouble;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
Classe de mapping d'un restaurantEntity vers son dto, pas possible de le faire dans le dto car besoin de récupèrer l'url publique de la photo
 */
@Service
public class RestaurantMapper {
    private final MinioService minioService;
    private final EvaluationMapper evaluationMapper;
    public RestaurantMapper(MinioService minioService, EvaluationMapper evaluationMapper, EvaluationMapper evaluationMapper1) {
        this.minioService = minioService;
        this.evaluationMapper = evaluationMapper1;
    }
    public RestaurantDto toDto(RestaurantEntity restaurantEntity) {
        if (restaurantEntity == null) {
            return null;
        }

        AtomicDouble moyenne = new AtomicDouble(-1);
        List<EvaluationDto> evaluationDtos;
        String url = restaurantEntity.getPhotoKey() != null && !restaurantEntity.getPhotoKey().isEmpty() ?  this.minioService.getPublicUrl(restaurantEntity.getPhotoKey()):"";
        if (restaurantEntity.getEvaluation() != null && !restaurantEntity.getEvaluation().isEmpty()) {
            moyenne.set(0);
            evaluationDtos = restaurantEntity.getEvaluation().stream().map(e -> {
                moyenne.addAndGet(e.getNote());
                return evaluationMapper.toDto(e);
            }).toList();
            moyenne.updateAndGet(v -> v / evaluationDtos.size());
        } else {
            evaluationDtos = new ArrayList<>();
        }

        return new RestaurantDto(
                restaurantEntity.getId(),
                restaurantEntity.getName(),
                restaurantEntity.getAddress(),
                moyenne.get(),
                evaluationDtos,
                url
        );
    }

}
