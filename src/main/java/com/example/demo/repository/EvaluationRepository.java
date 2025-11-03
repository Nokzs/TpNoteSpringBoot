package com.example.demo.repository;

import com.example.demo.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<EvaluationEntity,Long> {
    List<EvaluationEntity> findByEvaluatorName(String evaluatorName);
}
