package com.example.demo.service;

import com.example.demo.dto.evaluationDto.CreateEvaluationDto;
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

import java.util.List;

@Slf4j
@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final RestaurantRepository restaurantRepository;
    private final IndexService indexService;
    public EvaluationService(EvaluationRepository evaluationRepository, RestaurantRepository restaurantRepository, IndexService indexService, IndexService indexService1) {
        this.evaluationRepository = evaluationRepository;
        this.restaurantRepository = restaurantRepository;
        this.indexService = indexService;
    }

    public EvaluationEntity createEvalution(@NotNull @Valid CreateEvaluationDto createEvaluationDto, String name) {
        RestaurantEntity r = this.restaurantRepository.findById(createEvaluationDto.restaurantId()).orElseThrow(()-> new EntityNotFoundException("Restaurant not found"));
        List<EvaluationEntity> evaluationEntityList = r.getEvaluation();
        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setEvaluatorName(name);
        evaluationEntity.setNote(createEvaluationDto.note());
        evaluationEntity.setContent(createEvaluationDto.content());
        evaluationEntityList.add(evaluationEntity);
        evaluationEntity.setRestaurant(r);
        r.setEvaluation(evaluationEntityList);
        EvaluationEntity entity = this.evaluationRepository.save(evaluationEntity);
        this.indexService.indexEvaluation(createEvaluationDto.content(),evaluationEntity.getId());
        return entity;
    }
    @PreAuthorize("isAuthenticated()")
    public void deleteEvaluation(Long evaluationId) {
        if(!evaluationRepository.existsById(evaluationId)){
            throw new EntityNotFoundException("Evaluation not found");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> roles = jwt.getClaimAsStringList("roles");
        EvaluationEntity entity = this.evaluationRepository.findById(evaluationId).orElseThrow(()-> new EntityNotFoundException("Evaluation not found"));
        if(!roles.contains("ADMIN") || !entity.getEvaluatorName().equals(authentication.getName())){
            throw new AccessDeniedException("Vous n'avez le droit de supprimer cette évaluation");
        }

        this.evaluationRepository.deleteById(evaluationId);
    }

    public List<EvaluationEntity> getUserEvaluation(String name) {
        return this.evaluationRepository.findByEvaluatorName(name);
    }
    public EvaluationEntity findById(long id) {
        log.info("je cherche par id");
        return this.evaluationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Evaluation not found"));
    }
    public List<EvaluationEntity> findAll() {
        return this.evaluationRepository.findAll();
    }
}
