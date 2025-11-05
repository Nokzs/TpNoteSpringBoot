package com.example.evaluationTest;

import com.example.demo.dto.evaluationDto.CreateEvaluationDto;
import com.example.demo.entity.EvaluationEntity;
import com.example.demo.entity.RestaurantEntity;
import com.example.demo.repository.EvaluationRepository;
import com.example.demo.repository.RestaurantRepository;
import com.example.demo.service.EvaluationService;
import com.example.demo.utils.MinioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EvaluationServiceTest {
    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private EvaluationService evaluationService;
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvaluation_success() {
        CreateEvaluationDto dto = new CreateEvaluationDto( "Super resto",1,new ArrayList<>(), 3); // exemple
        String reviewerName = "TOTO";

        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(3L);

        EvaluationEntity savedEvaluation = new EvaluationEntity(dto, restaurant, reviewerName);

        when(restaurantRepository.findById(3L)).thenReturn(Optional.of(restaurant));
        when(evaluationRepository.save(any(EvaluationEntity.class))).thenReturn(savedEvaluation);

        EvaluationEntity result = evaluationService.createEvalution(dto, reviewerName);

        assertNotNull(result);
        assertEquals(reviewerName, result.getEvaluatorName());
        assertEquals(restaurant, result.getRestaurant());
        assertEquals(savedEvaluation.getContent(), result.getContent());
        verify(restaurantRepository).findById(3L);
        verify(restaurantRepository, times(1)).findById(dto.restaurantId());
        verify(evaluationRepository).save(any(EvaluationEntity.class));
    }

    @Test
    void createEvaluation_restaurantNotFound() {
        CreateEvaluationDto dto = new CreateEvaluationDto( "Super resto",1,new ArrayList<>(), 3L); // exemple
        String reviewerName = "TOTO";

        when(restaurantRepository.findById(3L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> evaluationService.createEvalution(dto, reviewerName));

        assertEquals("Restaurant not found", exception.getMessage());
        verify(restaurantRepository).findById(3L);
        verifyNoInteractions(evaluationRepository);
    }
    @Test
    void getUserEvaluation_shouldReturnEvaluation(){
        String name = "toto";
        List<EvaluationEntity> evaluations = new ArrayList<>();
        evaluations.add(new EvaluationEntity());
        evaluations.add(new EvaluationEntity());
        evaluations.add(new EvaluationEntity());
        when(evaluationRepository.findByEvaluatorName(name)).thenReturn(evaluations);
        List<EvaluationEntity> result = evaluationService.getUserEvaluation(name);
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(evaluationRepository, times(1)).findByEvaluatorName(name);
        when(evaluationRepository.findByEvaluatorName(name)).thenReturn(evaluations);
        verify(evaluationRepository,times(1)).findByEvaluatorName(name);
    }
    @Test
    void getUserEvaluation_shouldReturnEntityNotFoundException(){
        when(evaluationRepository.findByEvaluatorName("toto")).thenReturn(new ArrayList<>());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> evaluationService.getUserEvaluation("toto"));

        assertEquals("Evaluation not found", exception.getMessage());
        verify(evaluationRepository, times(1)).findByEvaluatorName("toto");
    }

    @Test
    void getUserEvaluation_shouldReturnOnlyEvaluationsForGivenUser() {
        String userName = "Toto";

        EvaluationEntity eval1 = new EvaluationEntity();
        eval1.setEvaluatorName(userName);

        EvaluationEntity eval2 = new EvaluationEntity();
        eval2.setEvaluatorName(userName);

        EvaluationEntity eval3 = new EvaluationEntity();
        eval3.setEvaluatorName("other");

        when(evaluationRepository.findByEvaluatorName(userName))
                .thenReturn(List.of(eval1, eval2));

        List<EvaluationEntity> result = evaluationService.getUserEvaluation(userName);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(e -> e.getEvaluatorName().equals(userName)));
        verify(evaluationRepository, times(1)).findByEvaluatorName(userName);
    }
    @Test
    void deleteEvaluation_shouldThrowEntityNotFound() {
        Long evaluationId = 1L;
        when(evaluationRepository.existsById(evaluationId)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> evaluationService.deleteEvaluation(evaluationId));

        assertEquals("Evaluation not found", ex.getMessage());
        verify(evaluationRepository, times(1)).existsById(evaluationId);
        verify(evaluationRepository, never()).findById(evaluationId);
        verify(evaluationRepository, never()).deleteById(anyLong());
    }
    @Test
    void deleteEvaluation_shouldThrowAccessDenied_whenNotAdminOrOwner() {
        Long evaluationId = 1L;
        EvaluationEntity entity = new EvaluationEntity();
        entity.setEvaluatorName("toto");
        entity.setKeys(List.of("key1"));

        when(evaluationRepository.existsById(evaluationId)).thenReturn(true);
        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(entity));

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwt);
         Map<String, Object> coursm2 = new HashMap<>();
        coursm2.put("roles", List.of("USER"));

        Map<String, Object> resourceAccess = new HashMap<>();
        resourceAccess.put("coursm2", coursm2);

        when(jwt.getClaim("resource_access")).thenReturn(resourceAccess);
        when(jwt.getClaimAsString("name")).thenReturn("Bob");

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> evaluationService.deleteEvaluation(evaluationId));

        assertEquals("Vous n'avez le droit de supprimer cette évaluation", ex.getMessage());
        verify(evaluationRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteEvaluation_shouldSucceed_forOwner() {
        Long evaluationId = 1L;
        EvaluationEntity entity = new EvaluationEntity();
        String name = "toto";
        entity.setEvaluatorName(name);
        entity.setKeys(List.of("key1", "key2"));

        when(evaluationRepository.existsById(evaluationId)).thenReturn(true);
        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(entity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(jwt);
        Map<String, Object> coursm2 = new HashMap<>();
        coursm2.put("roles", List.of("USER"));

        Map<String, Object> resourceAccess = new HashMap<>();
        resourceAccess.put("coursm2", coursm2);

        when(jwt.getClaim("resource_access")).thenReturn(resourceAccess);
        when(jwt.getClaimAsString("name")).thenReturn(name);

        List<String> keys = evaluationService.deleteEvaluation(evaluationId);

        assertEquals(2, keys.size());
        verify(evaluationRepository, times(1)).deleteById(evaluationId);
    }
    @Test
    void deleteEvaluation_shouldSucceed_forAdmin() {
        Long evaluationId = 1L;
        EvaluationEntity entity = new EvaluationEntity();
        String name = "toto";
        entity.setEvaluatorName(name);
        entity.setKeys(List.of("key1", "key2"));

        when(evaluationRepository.existsById(evaluationId)).thenReturn(true);
        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(entity));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwt);
        Map<String, Object> coursm2 = new HashMap<>();
        coursm2.put("roles", List.of("ADMIN"));

        Map<String, Object> resourceAccess = new HashMap<>();
        resourceAccess.put("coursm2", coursm2);

        when(jwt.getClaim("resource_access")).thenReturn(resourceAccess);
        when(jwt.getClaimAsString("name")).thenReturn("autre toto");
        List<String> keys = evaluationService.deleteEvaluation(evaluationId);
        assertEquals(2, keys.size());
        verify(evaluationRepository,times(1)).deleteById(evaluationId);
    }
    @Test
    void findyById_shouldThrowEntityNotFound() {
        Long evaluationId = 1L;
        EvaluationEntity entity = new EvaluationEntity();
        entity.setEvaluatorName("toto");
        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> evaluationService.deleteEvaluation(evaluationId));

        assertEquals("Evaluation not found", ex.getMessage());
        verify(evaluationRepository, never()).findById(evaluationId);
        verify(evaluationRepository,times(1)).existsById(evaluationId);
    }
    @Test
    void findyById_shouldReturnEntiyWhenExist() {
        Long evaluationId = 1L;
        EvaluationEntity entity = new EvaluationEntity();
        entity.setEvaluatorName("toto");
        entity.setId(1L);
        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(entity));
        EvaluationEntity result = evaluationService.findById(evaluationId);
        when(evaluationRepository.findById(evaluationId)).thenReturn(Optional.of(entity));
        assertEquals(entity.getEvaluatorName(), result.getEvaluatorName());
        assertEquals(entity.getId(), result.getId());
        verify(evaluationRepository, times(1)).findById(evaluationId);
    }

}
