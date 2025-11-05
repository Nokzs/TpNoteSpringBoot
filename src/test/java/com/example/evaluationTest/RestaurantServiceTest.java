package com.example.evaluationTest;

import com.example.demo.dto.restaurantDto.CreateRestaurantDto;
import com.example.demo.dto.restaurantDto.UpdateRestaurantDto;
import com.example.demo.entity.RestaurantEntity;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.service.RestaurantService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RestaurantServiceTest {
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createRestaurant_shouldSaveRestaurant_whenUserIsAdmin() {
        CreateRestaurantDto dto = new CreateRestaurantDto("restaurant 1","13 rue de la foret",null);

        RestaurantEntity savedEntity = new RestaurantEntity(dto);

        when(restaurantRepository.save(any(RestaurantEntity.class))).thenReturn(savedEntity);

        RestaurantEntity result = restaurantService.createRestaurant(dto);

        assertNotNull(result);
        assertEquals("restaurant 1", result.getName());
        verify(restaurantRepository, times(1)).save(any(RestaurantEntity.class));
    }
/*
    @Test
    @WithMockUser(roles = "USER")
    void createRestaurant_shouldThrowAccessDenied_whenUserIsNotAdmin() {
        CreateRestaurantDto dto = new CreateRestaurantDto("restaurant 1","13 rue de la foret",null);

        assertThrows(AccessDeniedException.class,
                () -> restaurantService.createRestaurant(dto));

        verify(restaurantRepository, never()).save(any(RestaurantEntity.class));
    }
*/
    @Test
    void findById_valid(){
        long id = 1L;
        RestaurantEntity restaurant =  new RestaurantEntity();
        restaurant.setId(id);
        when(restaurantRepository.findById(any())).thenReturn(Optional.of(restaurant));
        assertEquals(restaurant, restaurantService.findById(id));
        verify(restaurantRepository, times(1)).findById(any());
    }
    @Test
    void findById_shouldThrownNotFound_whenNotFound(){
        long id = 1L;
        RestaurantEntity restaurant =  new RestaurantEntity();
        restaurant.setId(id);
        when(restaurantRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> this.restaurantService.findById(id));
        verify(restaurantRepository, times(1)).findById(any());
    }

    @Test
    void updateRestaurant_shouldUpdateFields_whenRestaurantExists() {
        UpdateRestaurantDto dto = new UpdateRestaurantDto(1L, "Nouveau Nom", "Nouvelle Adresse");
        RestaurantEntity existing = new RestaurantEntity();
        existing.setId(1L);
        existing.setName("Ancien Nom");
        existing.setAddress("Ancienne Adresse");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));

        restaurantService.updateRestaurant(dto);

        verify(restaurantRepository).findById(1L);
        verify(restaurantRepository).save(existing);

        assertEquals("Nouveau Nom", existing.getName());
        assertEquals("Nouvelle Adresse", existing.getAddress());
    }
    @Test
    void updateRestaurant_shouldThrowException_whenRestaurantNotFound() {
        UpdateRestaurantDto dto = new UpdateRestaurantDto(99L, "Nom", "Adresse");
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> restaurantService.updateRestaurant(dto));

        verify(restaurantRepository, times(1)).findById(99L);
        verify(restaurantRepository, never()).save(any());
    }
    @Test
    void updateRestaurant_shouldOnlyUpdateNonNullFields() {
        UpdateRestaurantDto dto = new UpdateRestaurantDto(1L, null, "Nouvelle Adresse");
        RestaurantEntity existing = new RestaurantEntity();
        existing.setId(1L);
        existing.setName("Nom Original");
        existing.setAddress("Ancienne Adresse");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));

        restaurantService.updateRestaurant(dto);

        assertEquals("Nom Original", existing.getName());
        assertEquals("Nouvelle Adresse", existing.getAddress());
        verify(restaurantRepository).save(existing);
    }
    @Test
    void updateRestaurant_shouldOnlyUpdateNonNullFieldsAddress() {
        UpdateRestaurantDto dto = new UpdateRestaurantDto(1L, "palace", null);
        RestaurantEntity existing = new RestaurantEntity();
        existing.setId(1L);
        existing.setName("Nom Original");
        existing.setAddress("Ancienne Adresse");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));

        restaurantService.updateRestaurant(dto);

        assertEquals("palace", existing.getName());
        assertEquals("Ancienne Adresse", existing.getAddress());
        verify(restaurantRepository).save(existing);
    }
/*
    @Test
    @WithMockUser(roles = "USER")
    void updateRestaurant_shouldThrow_WhenNotAdmin(){
        UpdateRestaurantDto dto = new UpdateRestaurantDto(1L, "palace", null);
        RestaurantEntity existing = new RestaurantEntity();
        existing.setId(1L);
        existing.setName("Nom Original");
        existing.setAddress("Ancienne Adresse");

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));
        assertThrows(NoSuchElementException.class, () -> restaurantService.updateRestaurant(dto));
    }*/
}
