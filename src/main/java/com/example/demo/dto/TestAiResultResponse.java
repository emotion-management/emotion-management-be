package com.example.demo.dto;

public record TestAiResultResponse(
        Integer score,
        String summary,
        String advice
) {}
