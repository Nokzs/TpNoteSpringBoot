package com.example.demo.controller;

import com.example.demo.dto.restaurantDto.*;
import com.example.demo.entity.RestaurantEntity;
import com.example.demo.mapper.RestaurantMapper;
import com.example.demo.service.RestaurantService;
import com.example.demo.utils.MinioService;
import io.minio.errors.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;


@RestController()
@RequestMapping("/restaurant")
@Slf4j
public class RestaurantController {

   private final MinioService minioService;
   private final RestaurantService restaurantService;
   private final RestaurantMapper restaurantMapper;
    public RestaurantController(MinioService minioService, RestaurantService restaurantService, RestaurantMapper restaurantMapper) {
        this.minioService = minioService;
        this.restaurantService = restaurantService;
        this.restaurantMapper = restaurantMapper;
    }


    @GetMapping
    public List<RestaurantDto> FindAll(){
      List<RestaurantEntity> restaurant = this.restaurantService.findAll();
      return restaurant.stream().map(this.restaurantMapper::toDto).toList();
    }
    @GetMapping("/{id}")
    public RestaurantDto FindById(@PathVariable @Valid long id){
        RestaurantEntity restaurant = this.restaurantService.findById(id);
        return this.restaurantMapper.toDto(restaurant);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping()
    public RestaurantDto createRestaurant(@Valid @RequestBody CreateRestaurantDto createRestaurantDto){
        if(createRestaurantDto.url()!=null && !createRestaurantDto.url().isEmpty()){
            if(!this.minioService.objectExists("coursm2", createRestaurantDto.url())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "la clé de l'image n'est pas valide");

            }
        }
        RestaurantEntity restaurant = this.restaurantService.createRestaurant(createRestaurantDto);
        return this.restaurantMapper.toDto(restaurant);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping()
    public void updateRestaurant( @Valid @RequestBody  UpdateRestaurantDto updateRestaurantDto){
        this.restaurantService.updateRestaurant(updateRestaurantDto);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/signedUrl/")
    public RestaurantSignedUrlDto GetRestaurantSigned() {

        UUID id = UUID.randomUUID();
        try {
            String url = this.minioService.getSignedUrlRestaurant("restaurantPicture/"+id.toString());
            return new RestaurantSignedUrlDto(url,id.toString());
        } catch (ServerException | InternalException | XmlParserException | InvalidResponseException |
                 InvalidKeyException | NoSuchAlgorithmException | IOException | ErrorResponseException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

}
