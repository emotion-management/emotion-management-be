package com.example.demo.repository;

import com.example.demo.entity.EmotionTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionTestRepository extends JpaRepository<EmotionTest, Long> {
    Optional<EmotionTest> findByCode(String code);
}
