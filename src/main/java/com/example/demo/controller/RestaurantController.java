package com.example.demo.controller;

import com.example.demo.dto.restaurantDto.*;
import com.example.demo.entity.RestaurantEntity;
import com.example.demo.mapper.RestaurantMapper;
import com.example.demo.service.RestaurantService;
import com.example.demo.utils.MinioService;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
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

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurant récupéré avec succès"),
    })
    @Operation(description = "récupère tous les restaurant")
    @GetMapping
    public List<RestaurantDto> FindAll(){
      List<RestaurantEntity> restaurant = this.restaurantService.findAll();
      return restaurant.stream().map(this.restaurantMapper::toDto).toList();
    }
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurant récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Restaurant non trouvé"),
    })
    @Operation(description = "cherche un restaurant par son ID")
    @GetMapping("/{id}")
    public RestaurantDto FindById(@PathVariable @Valid long id){
        RestaurantEntity restaurant = this.restaurantService.findById(id);
        return this.restaurantMapper.toDto(restaurant);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "restaurant créer avec succès"),
            @ApiResponse(responseCode = "400", description = "photo inexistante")
    })
    @Operation(description = "creation d'un restaurant")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN')")
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

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "restaurant créer avec succès"),
            @ApiResponse(responseCode = "404", description = "restaurant non trouvé")
    })
    @Operation(description = "Mise à jour d'un restaurant")
    @PreAuthorize("isAuthenticated()")
    @PutMapping()
    @Secured("ADMIN")
    public void updateRestaurant( @Valid @RequestBody  UpdateRestaurantDto updateRestaurantDto){
        this.restaurantService.updateRestaurant(updateRestaurantDto);
    }
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "restaurant créer avec succès"),
            @ApiResponse(responseCode = "400", description = "photo inexistante")
    })
    @Operation(description = "Récupération d'une url signée pour un restaurant")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/signedUrl/")
    @Secured("ADMIN")
    public RestaurantSignedUrlDto GetRestaurantSigned() {

        UUID id = UUID.randomUUID();
        try {
            String url = this.minioService.getSignedUrlRestaurant(id.toString());
            return new RestaurantSignedUrlDto(url,"restaurantPicture" + id);
        } catch (ServerException | InternalException | XmlParserException | InvalidResponseException |
                 InvalidKeyException | NoSuchAlgorithmException | IOException | ErrorResponseException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

}
