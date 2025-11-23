package com.example.demo.controller;

import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserAccountRepository userAccountRepository;

    // 단순히 email 기반으로 찾는 샘플 (실서비스는 인증 토큰 필요)
    @GetMapping
    public UserAccount getProfile(@RequestParam String email) {
        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
    }

    @PutMapping
    public UserAccount update(@RequestParam String email, @RequestBody SignupRequest request) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getFeedbackStyle() != null) user.setFeedbackStyle(request.getFeedbackStyle());
        if (request.getNotificationAm() != null) user.setNotificationAm(request.getNotificationAm());
        if (request.getNotificationTime() != null) user.setNotificationTime(LocalTime.parse(request.getNotificationTime()));
        return userAccountRepository.save(user);
    }
}
