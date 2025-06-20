package com.chatbot.model.service;

import com.chatbot.model.config.GeminiApiConfig;
import com.chatbot.model.dto.gemini.Content;
import com.chatbot.model.dto.gemini.GeminiRequest;
import com.chatbot.model.dto.gemini.GeminiResponse;
import com.chatbot.model.dto.gemini.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiApiClient {

    private final WebClient.Builder webClientBuilder;
    private final GeminiApiConfig apiConfig;

    public String generateContent(String prompt) {
        // O WebClient é construído sem a URL base aqui
        WebClient webClient = webClientBuilder.build();

        // Monta o corpo da requisição no formato que a API do Gemini espera
        var requestBody = new GeminiRequest(
            List.of(new Content(List.of(new Part(prompt))))
        );

        try {
            // Faz a chamada POST para a API
            GeminiResponse response = webClient.post()
                    // A URL completa e o parâmetro da chave são definidos aqui
                    .uri(apiConfig.getUrl(), uriBuilder -> uriBuilder
                        .queryParam("key", apiConfig.getKey())
                        .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Mono.just(requestBody), GeminiRequest.class)
                    .retrieve()
                    .bodyToMono(GeminiResponse.class)
                    .block(); // .block() torna a chamada síncrona para simplificar

            // Extrai o texto da resposta
            // Adicionado null-checks para maior segurança
            if (response != null && response.candidates() != null && !response.candidates().isEmpty()) {
                return response.candidates().get(0).content().parts().get(0).text();
            }
            return "Não foi possível obter uma resposta da IA.";

        } catch (Exception e) {
            System.err.println("Erro ao chamar a API do Gemini: " + e.getMessage());
            return "Desculpe, não consegui processar sua solicitação no momento.";
        }
    }
}