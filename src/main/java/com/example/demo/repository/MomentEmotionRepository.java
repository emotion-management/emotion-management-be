package com.example.demo.repository;

import com.example.demo.entity.MomentEmotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MomentEmotionRepository extends JpaRepository<MomentEmotion, Long> {
    // 캘린더에서 월별 조회할 때 쓸 것
    List<MomentEmotion> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<MomentEmotion> findAllByRecordedAtBetween(LocalDateTime start, LocalDateTime end);
}
