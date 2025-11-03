package com.example.demo.controller;


import com.example.demo.dto.evaluationDto.CreateEvaluationDto;
import com.example.demo.dto.evaluationDto.EvaluationDto;
import com.example.demo.dto.evaluationDto.EvaluationSignedUrlDto;
import com.example.demo.entity.EvaluationEntity;
import com.example.demo.service.EvaluationService;
import com.example.demo.service.IndexService;
import com.example.demo.utils.MinioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController()
@RequestMapping("/evaluation")
@Slf4j
public class EvaluationController {
    private final EvaluationService evaluationService;
    private final IndexService indexService;
    private final MinioService minioService;
    public EvaluationController(EvaluationService evaluationService, IndexService indexService, MinioService minioService){
        this.evaluationService = evaluationService;
        this.indexService = indexService;
        this.minioService = minioService;
    }
   @PostMapping()
   @PreAuthorize("isAuthenticated()")
   @Secured("ADMIN,USER")
    public EvaluationDto createEvaluation(@Valid @RequestBody CreateEvaluationDto createEvaluationDto, Authentication authentication) throws Exception{
        //cas par défaut
        String name = "unknown";
        // On ne renvoie pas d'erreur car cela est fait automatiquement dans le cas où ce n'est pas un JWT
        if ((authentication.getPrincipal() instanceof Jwt jwt)) {
            name = jwt.getClaimAsString("name");
       }
       EvaluationEntity evaluation = this.evaluationService.createEvalution(createEvaluationDto,name);
       return EvaluationDto.buildFromEntity(evaluation);
    }
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void deleteEvaluation(@PathVariable Long id){
        this.evaluationService.deleteEvaluation(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping()
    public List<EvaluationDto> getUserEvaluations(Authentication authentication){
        String name = "unknown";
        Jwt token = null;
        List<EvaluationEntity> evaluations;
        if(authentication.getPrincipal() instanceof Jwt jwt){
            token = jwt;
            name = token.getClaimAsString("name");
        }
        evaluations = this.evaluationService.getUserEvaluation(name);

        return evaluations.stream().map(EvaluationDto::buildFromEntity).toList();
    }
    @GetMapping("/byWords")
    public  List<EvaluationDto> getEvaluationsByWord(@RequestParam ArrayList<String> words){
        List<Long> ids =  this.indexService.searchEvaluation(words);
        return ids.stream().map(evaluationService::findById).toList().stream().map(EvaluationDto::buildFromEntity).toList();
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/evaluationsignedurl")
    public EvaluationSignedUrlDto getEvaluationSignedUrl(@RequestParam int nbPhotos){
        String key = UUID.randomUUID().toString();
        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < nbPhotos; i++) {

            keys.add("eval" + "-" + key + "-" + i);

        }
        List<String> urls = this.minioService.getSignedUrlEvaluation(keys,nbPhotos);
        return new EvaluationSignedUrlDto(keys,urls);
    }

}
