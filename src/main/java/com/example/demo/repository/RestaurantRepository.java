package com.example.demo.repository;

import com.example.demo.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity,Long> {
    Long id(Long id);
}
