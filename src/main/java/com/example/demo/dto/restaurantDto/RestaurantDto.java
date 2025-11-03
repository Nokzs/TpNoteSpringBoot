package com.example.demo.dto.restaurantDto;

import com.example.demo.dto.evaluationDto.EvaluationDto;
import com.example.demo.entity.RestaurantEntity;
import com.google.common.util.concurrent.AtomicDouble;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record RestaurantDto(Long id, String name, String address, double moyenne, List<EvaluationDto> evaluation,String url ) {

    public static  RestaurantDto buildFromEntity(RestaurantEntity restaurantEntity,String url){
        AtomicDouble moyenne = new AtomicDouble(-1);
        List<EvaluationDto> evaluationDtos;
        if(!restaurantEntity.getEvaluation().isEmpty()) {
            moyenne.set(0);
            evaluationDtos = restaurantEntity.getEvaluation().stream().map(e -> {
                            moyenne.addAndGet(e.getNote());
                            return EvaluationDto.buildFromEntity(e);
            }).toList();
            moyenne.updateAndGet(v -> v / evaluationDtos.size());
        } else {
            evaluationDtos = new ArrayList<>();
        }

        return new RestaurantDto(restaurantEntity.getId(), restaurantEntity.getName(), restaurantEntity.getAddress(),moyenne.get(),evaluationDtos,url);
    }
}
