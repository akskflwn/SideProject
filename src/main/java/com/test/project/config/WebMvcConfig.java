package com.test.project.config;

import static com.test.project.constants.CorsConstants.ALLOWED_HEADERS;
import static com.test.project.constants.CorsConstants.ALLOWED_METHODS;
import static com.test.project.constants.CorsConstants.ALLOWED_ORIGIN_PATTERNS;
import static com.test.project.constants.CorsConstants.MAX_AGE_SECOND;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 스프링 빈(Bean) 으로 등록
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS)
                .allowedMethods(ALLOWED_METHODS)
                .allowedHeaders(ALLOWED_HEADERS)
                .maxAge(MAX_AGE_SECOND);
    }
}