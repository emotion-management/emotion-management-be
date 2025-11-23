package com.example.demo.service;

import com.example.demo.dto.SignupRequest;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;

    public UserAccount signup(SignupRequest request) {
        userAccountRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email already exists");
        });
        UserAccount user = new UserAccount();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // 실서비스에서는 반드시 암호화 필요
        user.setNickname(request.getNickname());
        user.setFeedbackStyle(request.getFeedbackStyle());
        user.setNotificationAm(request.getNotificationAm());
        if (request.getNotificationTime() != null) {
            user.setNotificationTime(LocalTime.parse(request.getNotificationTime()));
        }
        return userAccountRepository.save(user);
    }

    public UserAccount login(String email, String password) {
        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials"));
        if (!user.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid credentials");
        }
        return user;
    }
}
