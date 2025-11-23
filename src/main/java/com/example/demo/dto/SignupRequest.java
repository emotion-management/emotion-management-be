package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String nickname;
    private AdviceType feedbackStyle; // COMFORT/REALITY
    private Boolean notificationAm; // true 오전, false 오후
    private String notificationTime; // HH:mm
}
