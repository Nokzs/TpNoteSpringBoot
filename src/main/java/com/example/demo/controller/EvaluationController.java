package com.example.demo.controller;


import com.example.demo.dto.evaluationDto.CreateEvaluationDto;
import com.example.demo.dto.evaluationDto.EvaluationDto;
import com.example.demo.dto.evaluationDto.EvaluationSignedUrlDto;
import com.example.demo.dto.evaluationDto.UpdateEvaluationDto;
import com.example.demo.entity.EvaluationEntity;
import com.example.demo.mapper.EvaluationMapper;
import com.example.demo.service.EvaluationService;
import com.example.demo.service.IndexService;
import com.example.demo.utils.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Évaluations", description = "Gestion des évaluations des restaurants")
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
    @Operation(summary = "Créer une évaluation", description = "Ajoute une évaluation pour un restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Évaluation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Erreur de validation")
    })
   @PostMapping()
   @PreAuthorize("isAuthenticated()")
    public EvaluationDto createEvaluation(
            @Parameter(description = "Données de création de l'évaluation")
            @Valid @RequestBody CreateEvaluationDto createEvaluationDto,
            Authentication authentication) throws Exception{
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
    @Operation(summary = "Supprimer une évaluation", description = "Supprime une évaluation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Évaluation créée avec succès"),
            @ApiResponse(responseCode =  "403", description = "Roles insuffisants")
    })
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public void deleteEvaluation(

            @Parameter(description = "Id de l'évaluation")
            @PathVariable Long id
    ){
        List<String> keys = this.evaluationService.deleteEvaluation(id);
        if(!keys.isEmpty()){
            for(String key : keys){
                this.minioService.deleteObject(key);
            }
        }
    }

    @Operation(summary = "Mise à jour d'une évaluation", description = "mettre à jour une évaluation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Évaluation modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "photo inexistante"),
            @ApiResponse(responseCode =  "403", description = "Roles insuffisants")
    })
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public void updateEvaluation(
            @Parameter(description = "id de l'évaluation")
            @PathVariable Long id,
            @Parameter(description = "Donnès de mise à jour")
            UpdateEvaluationDto dto){
        if(dto.keys()!=null){
            for(String key : dto.keys()){
                if(!key.isEmpty() && !this.minioService.objectExists("coursm2",key)){
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "la clé de l'image n'est pas valide");
                }
            }
        }
       List<String> keys =  this.evaluationService.updateEvalution(id,dto);
        if(!keys.isEmpty()){
            for(String key : keys){
                this.minioService.deleteObject(key);
            }
        }

    }


    @Operation(summary = "Récupère les évaluation de l'utlisateur connéctée", description = "Récupère les évaluation de l'utlisateur connéctée")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Évaluation récupéré avec succès"),
            @ApiResponse(responseCode =  "403", description = "Roles insuffisants")
    })
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

        return evaluations.stream().map(this.evaluationMapper::toDto).toList();
    }
    @Operation(summary = "Récupère les évaluation avec des mots clés", description = "Cherche des évaluation par mots clés")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Évaluation récupéré avec succès"),
    })
    @GetMapping("/byWords")
    public  List<EvaluationDto> getEvaluationsByWord(
            @Parameter(description = "Mots recherchés")
            @RequestParam ArrayList<String> words){

        List<Long> ids =  this.indexService.searchEvaluation(words);
        return ids.stream().map(evaluationService::findById).toList().stream().map(evaluationMapper::toDto).toList();
    }

    @Operation(summary = "Fournis une url signée", description = "Fournis une url signée, l'utilisateur doit d'abord upload avant de créer l'évaluation et envoyé les clé envoyé par cette route")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Évaluation récupéré avec succès"),
            @ApiResponse(responseCode =  "401", description = "utilisateur non connécté")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/evaluationsignedurl")
    public EvaluationSignedUrlDto getEvaluationSignedUrl(
            @Parameter(description = "nombre de photos qui vont être insérés dans l'évaluation")
            @RequestParam int nbPhotos){
        String key = UUID.randomUUID().toString();
        ArrayList<String> keys = new ArrayList<>();
        for (int i = 0; i < nbPhotos; i++) {

            keys.add("eval" + "-" + key + "-" + i);

        }
        List<String> urls = this.minioService.getSignedUrlEvaluation(keys,nbPhotos);
        return new EvaluationSignedUrlDto(keys,urls);
    }

}
