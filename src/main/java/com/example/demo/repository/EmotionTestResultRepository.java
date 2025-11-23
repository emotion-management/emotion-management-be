package com.example.demo.repository;

import com.example.demo.entity.EmotionTestResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmotionTestResultRepository extends JpaRepository<EmotionTestResult, Long> {
    List<EmotionTestResult> findAllByTest_IdOrderByCreatedAtDesc(Long testId);
}
