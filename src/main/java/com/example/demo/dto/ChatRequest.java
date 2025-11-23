package com.example.demo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Getter @NoArgsConstructor
public class ChatRequest {
    private String model;
    private List<Message> messages;

    public ChatRequest(String model, String content) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("user", content));
    }

    @Getter @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}