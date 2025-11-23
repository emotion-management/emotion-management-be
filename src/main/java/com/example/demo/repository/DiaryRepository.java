package com.example.demo.repository;

import com.example.demo.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}