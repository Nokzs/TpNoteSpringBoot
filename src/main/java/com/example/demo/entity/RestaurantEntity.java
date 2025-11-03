package com.example.demo.entity;

import com.example.demo.dto.restaurantDto.CreateRestaurantDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "restaurant")
@Data

public class RestaurantEntity {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    String name;
     @Column(name="address")
     String address;

     @OneToMany(mappedBy = "restaurant")
    List<EvaluationEntity> evaluation = new ArrayList<>();

     @Column(name = "photoKey")
     String photoKey;

     public RestaurantEntity(CreateRestaurantDto createRestaurantDto) {
         this.name = createRestaurantDto.name();
         this.address = createRestaurantDto.address();
         this.photoKey = createRestaurantDto.url();

     }

    public RestaurantEntity() {

    }
}
