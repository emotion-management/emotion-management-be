package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import com.example.demo.dto.AdviceType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password; // 간단 저장 (실서비스에서는 반드시 암호화 필요)

    private String nickname;

    @Enumerated(EnumType.STRING)
    private AdviceType feedbackStyle; // COMFORT or REALITY

    private Boolean notificationAm; // true=오전, false=오후

    private LocalTime notificationTime; // HH:mm
}
