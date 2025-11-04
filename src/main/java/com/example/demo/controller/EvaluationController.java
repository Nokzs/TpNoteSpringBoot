package com.example.demo.controller;


import com.example.demo.dto.evaluationDto.CreateEvaluationDto;
import com.example.demo.dto.evaluationDto.EvaluationDto;
import com.example.demo.dto.evaluationDto.EvaluationSignedUrlDto;
import com.example.demo.entity.EvaluationEntity;
import com.example.demo.mapper.EvaluationMapper;
import com.example.demo.service.EvaluationService;
import com.example.demo.service.IndexService;
import com.example.demo.utils.MinioService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final EvaluationMapper evaluationMapper;
    public EvaluationController(EvaluationService evaluationService, IndexService indexService, MinioService minioService, EvaluationMapper evaluationMapper){
        this.evaluationService = evaluationService;
        this.indexService = indexService;
        this.minioService = minioService;
        this.evaluationMapper = evaluationMapper;
    }

   @PostMapping()
   @PreAuthorize("isAuthenticated()")
    public EvaluationDto createEvaluation(@Valid @RequestBody CreateEvaluationDto createEvaluationDto, Authentication authentication) throws Exception{
        /*
        Comme l'utilsateur doit envoyer les clés après avoir upload, on vérifie qu'il l'a bien fait, en regardant si un objet existe
        si une clé est invalide, on envoie une erreur
       */
       if(createEvaluationDto.keys()!=null){
           for(String key : createEvaluationDto.keys()){
               if(!key.isEmpty() && !this.minioService.objectExists("coursm2",key)){
                   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "la clé de l'image n'est pas valide");
               }
           }
       }
        /*
        on accepte que les jwt donc name aura une valeur, dans le cas ou on accepte plusieurs types d'authentification, il faudrait
        gérer tous les cas
       */

        String name = "";
        if ((authentication.getPrincipal() instanceof Jwt jwt)) {
            name = jwt.getClaimAsString("name");
        }

       EvaluationEntity evaluation = this.evaluationService.createEvalution(createEvaluationDto,name);
       this.indexService.indexEvaluation(createEvaluationDto.content(),evaluation.getId());

       return evaluationMapper.toDto(evaluation);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void deleteEvaluation(@PathVariable Long id){
        //TODO : supprimer les object
        List<String> key = this.evaluationService.deleteEvaluation(id);
        if(!key.isEmpty()){
            for(String key1 : key){

            }
        }
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

        return evaluations.stream().map((e)->{
            List<String> url = new ArrayList<>();

            if (e.getKeys() != null) {
                url = e.getKeys().stream().map(this.minioService::getPublicUrl).toList();
            }
           return EvaluationDto.buildFromEntity(e,url);
        }).toList();
    }
    @GetMapping("/byWords")
    public  List<EvaluationDto> getEvaluationsByWord(@RequestParam ArrayList<String> words){
        List<Long> ids =  this.indexService.searchEvaluation(words);
        return ids.stream().map(evaluationService::findById).toList().stream().map((e)->{
             List<String> url = new ArrayList<>();

            if (e.getKeys() != null) {
                url = e.getKeys().stream().map(this.minioService::getPublicUrl).toList();
            }
           return EvaluationDto.buildFromEntity(e,url);
        }).toList();
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
