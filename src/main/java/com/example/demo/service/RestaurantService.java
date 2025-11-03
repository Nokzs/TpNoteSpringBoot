package com.example.demo.service;

import com.example.demo.dto.restaurantDto.CreateRestaurantDto;
import com.example.demo.dto.restaurantDto.IdDto;
import com.example.demo.dto.restaurantDto.UpdateRestaurantDto;
import com.example.demo.entity.RestaurantEntity;
import com.example.demo.repository.RestaurantRepository;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Secured("ADMIN")
    public RestaurantEntity createRestaurant(CreateRestaurantDto restaurantDto){
        RestaurantEntity restaurantEntity = new RestaurantEntity(restaurantDto);


        return restaurantRepository.save(restaurantEntity
        );
    }
    public RestaurantEntity findById(long id){
       return this.restaurantRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public List<RestaurantEntity> findAll(){
        return this.restaurantRepository.findAll();
    }

    @Secured("ADMIN")
    public void updateRestaurant(UpdateRestaurantDto updateRestaurantDto) {
        RestaurantEntity resto = this.restaurantRepository.findById(updateRestaurantDto.id()).orElseThrow(()-> new NoSuchElementException("aucun restaurant avec cette Id"));
        resto.setName(updateRestaurantDto.name());
        resto.setAddress(updateRestaurantDto.address());
        this.restaurantRepository.save(resto);
    }
}
