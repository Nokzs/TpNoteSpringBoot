package com.example.demo.entity;

import com.example.demo.dto.restaurantDto.CreateRestaurantDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "restaurant")
@Data

public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "id du restaurant")
    private Long id;

    @Column(name = "name")
    String name;
     @Column(name="address")
     String address;

     @OneToMany(mappedBy = "restaurant")
    List<EvaluationEntity> evaluation = new ArrayList<>();

     @Column(name = "photo_key")
     String photokey;

     public RestaurantEntity(CreateRestaurantDto createRestaurantDto) {
         this.name = createRestaurantDto.name();
         this.address = createRestaurantDto.address();
         this.photokey = createRestaurantDto.url();

     }

    public RestaurantEntity() {

    }
}
