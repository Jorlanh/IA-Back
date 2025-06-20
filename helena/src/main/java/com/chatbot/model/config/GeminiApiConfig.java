package com.chatbot.model.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gemini.api")
@Data // Anotação do Lombok para gerar getters/setters
public class GeminiApiConfig {
    private String url;
    private String key;
}