package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 주소(API)에 대해서
                .allowedOrigins("*") // 모든 출처(Origin) 허용 (해커톤이니까 쿨하게 다 열어줍니다)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .maxAge(3000); // 원하는 시간만큼 pre-flight 리퀘스트 캐싱
    }
}