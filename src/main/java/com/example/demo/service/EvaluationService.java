package com.example.demo.service;

import com.example.demo.dto.evaluationDto.CreateEvaluationDto;
import com.example.demo.dto.evaluationDto.UpdateEvaluationDto;
import com.example.demo.entity.EvaluationEntity;
import com.example.demo.entity.RestaurantEntity;
import com.example.demo.repository.EvaluationRepository;
import com.example.demo.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;
    public EvaluationService(EvaluationRepository evaluationRepository, RestaurantRepository restaurantRepository) {
        this.evaluationRepository = evaluationRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public EvaluationEntity createEvalution(@NotNull @Valid CreateEvaluationDto createEvaluationDto, String name) {
        RestaurantEntity r = this.restaurantRepository.findById(createEvaluationDto.restaurantId()).orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        EvaluationEntity evaluationEntity = new EvaluationEntity(createEvaluationDto,r,name);
        return this.evaluationRepository.save(evaluationEntity);
    }
    @PreAuthorize("isAuthenticated()")
    public List<String> deleteEvaluation(Long evaluationId) {
        if(!evaluationRepository.existsById(evaluationId)){
            throw new EntityNotFoundException("Evaluation not found");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> roles = new ArrayList<>();

        // je dois recupérer le role afin de vérifier si l'utilisateur à bien le droit de supprimer
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("coursm2")) {
            Map<String, Object> coursm2 = (Map<String, Object>) resourceAccess.get("coursm2");
            if (coursm2.containsKey("roles")) {
                roles.addAll((List<String>) coursm2.get("roles"));
            }
        }
        EvaluationEntity entity = this.evaluationRepository.findById(evaluationId).orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));
        List<String> key = entity.getKeys();
        if(!roles.contains("ADMIN") && !entity.getEvaluatorName().equals(jwt.getClaimAsString("name"))){
            throw new AccessDeniedException("Vous n'avez le droit de supprimer cette évaluation");
        }

        this.evaluationRepository.deleteById(evaluationId);
        return Objects.requireNonNullElseGet(key, ArrayList::new);
    }

    public List<EvaluationEntity> getUserEvaluation(String name) {
        List<EvaluationEntity> entity = this.evaluationRepository.findByEvaluatorName(name);
        if(entity.isEmpty()){
            throw new EntityNotFoundException("Evaluation not found");
        }
        return entity;
    }
    public EvaluationEntity findById(long id) {
        log.info("l'id " + String.valueOf(id));
        return this.evaluationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));
    }
    public List<EvaluationEntity> findAll() {
        return this.evaluationRepository.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    public List<String> updateEvalution(Long id, UpdateEvaluationDto dto) {
        if(!evaluationRepository.existsById(id)){
            throw new EntityNotFoundException("Evaluation not found");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> roles = new ArrayList<>();
        // je dois recupérer le role afin de vérifier si l'utilisateur à bien le droit de supprimer
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null && resourceAccess.containsKey("coursm2")) {
            Map<String, Object> coursm2 = (Map<String, Object>) resourceAccess.get("coursm2");
            if (coursm2.containsKey("roles")) {
                roles.addAll((List<String>) coursm2.get("roles"));
            }
        }
        EvaluationEntity entity = this.evaluationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));
        List<String> key = entity.getKeys();
        if(!roles.contains("ADMIN") && !entity.getEvaluatorName().equals(jwt.getClaimAsString("name"))){
            throw new AccessDeniedException("Vous n'avez le droit de supprimer cette évaluation");
        }
        if(!dto.content().isEmpty()){
            entity.setContent(dto.content());
        }
        if(!dto.keys().isEmpty()){
            entity.setKeys(dto.keys());
        }
        if(dto.note() != null){
            entity.setNote(dto.note());
        }
        this.evaluationRepository.save(entity);
        return key;
    }

}
