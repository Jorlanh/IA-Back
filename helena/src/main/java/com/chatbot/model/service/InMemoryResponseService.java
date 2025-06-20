package com.chatbot.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class InMemoryResponseService {

    private final Map<String, String> predefinedResponses = new HashMap<>();

    @PostConstruct
    public void init() {
        predefinedResponses.put("Excelente! Por favor, cole o texto do aluno na caixa de digitação abaixo para que eu possa iniciar a análise.",
                "Estou pronta para analisar. Por favor, insira o texto do aluno e eu farei uma avaliação inicial com base nos padrões psicopedagógicos que conheço.");

        predefinedResponses.put("Eu sou uma ferramenta de triagem para apoiar o trabalho pedagógico. Analiso textos para encontrar padrões de dificuldades de aprendizagem, mas não forneço diagnósticos definitivos.",
                "Este sistema foi desenvolvido como uma ferramenta de apoio para educadores. Ele utiliza IA para identificar possíveis indicadores de dificuldades de aprendizagem, com base em textos fornecidos. O objetivo é oferecer uma triagem inicial, e não um diagnóstico. A avaliação final deve sempre ser conduzida por um profissional qualificado.");

        predefinedResponses.put("Posso identificar indícios de Dislexia, Discalculia, dificuldades de interpretação, problemas na expressão escrita, déficit de atenção, entre outros.",
                "Minha análise é focada em encontrar padrões textuais que possam sugerir dificuldades como:\n" +
                "- **Dislexia e Disortografia:** Troca de fonemas, inversão de sílabas.\n" +
                "- **Interpretação:** Dificuldade em extrair a ideia central do texto.\n" +
                "- **Expressão Escrita:** Falta de coesão e coerência.\n" +
                "- **Discalculia:** Problemas com raciocínio lógico-matemático em problemas escritos.\n" +
                "- **Déficit de Atenção:** Respostas desfocadas ou incompletas.");
    }

    public Optional<String> getResponseFor(String userInput) {
        return Optional.ofNullable(predefinedResponses.get(userInput));
    }
}