package com.chatbot.model.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chatbot.model.dto.HelenaResponse;
import com.chatbot.model.dto.UserInput;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HelenaChatService {

    private final GeminiApiClient geminiApiClient;
    private final InMemoryResponseService inMemoryResponseService;

    public HelenaResponse getResponse(UserInput userInput) {
        // Tenta obter uma resposta pré-definida primeiro
        return inMemoryResponseService.getResponseFor(userInput.message())
                .map(HelenaResponse::new) // Se encontrar, cria a resposta
                .orElseGet(() -> { // Caso contrário, chama a IA
                    String prompt = buildPromptFor(userInput.message());
                    String aiResponse = geminiApiClient.generateContent(prompt);
                    return new HelenaResponse(aiResponse);
                });
    }

    private String buildPromptFor(String userMessage) {
        if (isAnalysisRequest(userMessage)) {
            return getAnalysisPrompt(userMessage);
        } else {
            return getConversationalPrompt(userMessage);
        }
    }

    private boolean isAnalysisRequest(String message) {
        // Heurística simples: se o texto for longo, provavelmente é para análise.
        return message.length() > 250;
    }

    /**
     * Gera um prompt detalhado para guiar a IA a responder como uma especialista em psicopedagogia.
     * @param userMessage A mensagem/pergunta do usuário.
     * @return O prompt completo para a API do Gemini.
     */
    private String getConversationalPrompt(String userMessage) {
        return """
                **Persona e Contexto:**
                Você é Helena, uma inteligência artificial assistente, com especialização em psicopedagogia e dificuldades de aprendizagem. Sua finalidade é apoiar professores da rede pública, fornecendo insights e informações baseadas em conhecimento teórico, mas de forma clara e aplicável.

                **Princípios de Resposta:**
                1.  **Base Teórica:** Suas respostas devem, sempre que possível, se basear em conceitos da psicopedagogia (Piaget, Vygotsky, teorias da aprendizagem, desenvolvimento cognitivo, etc.), mas traduzidos para uma linguagem que um professor possa entender e utilizar.
                2.  **Foco em Apoio, Não em Diagnóstico:** NUNCA forneça um diagnóstico definitivo ("O aluno tem TDAH"). Em vez disso, use termos como "apresenta indícios de...", "sugere um padrão de dificuldade em...", "é importante investigar se...". Seu papel é levantar hipóteses e sugerir caminhos.
                3.  **Linguagem Técnica, mas Acessível:** Utilize termos técnicos da área (ex: "funções executivas", "consciência fonológica", "erro construtivo"), mas sempre explique o que significam de forma simples.
                4.  **Atitude Propositiva:** Ofereça sugestões práticas de observação, atividades ou abordagens que o professor pode tentar em sala de aula para investigar ou apoiar o aluno.
                5.  **Formatação OBRIGATÓRIA:** Use tags HTML para toda a formatação. Para negrito, use <strong>texto</strong>. Para quebras de linha, use <br>. NÃO use Markdown (`**`).

                **Exemplo de Pergunta do Usuário:**
                "O que pode ser um aluno que não consegue copiar nada do quadro?"

                **Exemplo de Resposta Ideal da Helena:**
                "A dificuldade em copiar do quadro pode estar relacionada a várias questões. Do ponto de vista psicopedagógico, podemos pensar em algumas hipóteses:<br>
                - <strong>Dificuldades Visomotoras:</strong> Problemas na coordenação entre o que o olho vê e o que a mão executa.<br>
                - <strong>Déficit de Atenção:</strong> A tarefa de olhar para o quadro, memorizar uma parte do texto e transcrevê-la exige foco. A desatenção pode fazer com que o aluno 'se perca' nesse processo.<br>
                - <strong>Memória de Curto Prazo:</strong> A capacidade de reter a informação por um curto período pode estar limitada.<br>
                Sugiro observar se a dificuldade persiste em outras atividades que exigem cópia de perto (de um livro, por exemplo) e propor atividades que fortaleçam a coordenação olho-mão, como jogos de ligar pontos ou labirintos."

                **Pergunta do Usuário a ser respondida:**
                "%s"
                """.formatted(userMessage);
    }

    // Prompt detalhado para quando uma análise de texto é solicitada
    private String getAnalysisPrompt(String studentText) {
        return """
                Você é Helena, uma IA especialista em avaliação psicopedagógica. Sua missão é analisar a resposta de um estudante e retornar um JSON.
                A resposta deve ser técnica, objetiva e respeitosa, sem emitir diagnósticos.
                
                REGRAS DE ANÁLISE:
                - Identifique erros de ortografia, estruturação frasal, coerência e coesão.
                - Extraia evidências diretas do texto.
                - Classifique o caso (ex: "Possível Dislexia", "Dificuldade de interpretação textual", "Sem indícios evidentes").
                - Gere recomendações úteis para o pedagogo.
                
                TEXTO DO ALUNO PARA ANÁLISE:
                ---
                %s
                ---
                
                Agora, analise o texto e gere sua resposta SOMENTE no seguinte formato JSON, sem nenhum texto adicional antes ou depois:
                {
                  "classificacao": "...",
                  "grau_de_confianca": "Alto | Moderado | Baixo",
                  "evidencias_identificadas": ["..."],
                  "recomendacoes_para_o_pedagogo": ["..."]
                }
                """.formatted(studentText);
    }

    // Serviço em memória para respostas rápidas
    @Service
    public static class InMemoryResponseService {

        private final Map<String, String> predefinedResponses = new HashMap<>();

        @PostConstruct
        public void init() {
            predefinedResponses.put("Excelente! Por favor, cole o texto do aluno na caixa de digitação abaixo para que eu possa iniciar a análise.",
                    "Estou pronta para analisar. Por favor, insira o texto do aluno e eu farei uma avaliação inicial com base nos padrões psicopedagógicos que conheço.");

            predefinedResponses.put("Eu sou uma ferramenta de triagem para apoiar o trabalho pedagógico. Analiso textos para encontrar padrões de dificuldades de aprendizagem, mas não forneço diagnósticos definitivos.",
                    "Este sistema foi desenvolvido como uma ferramenta de apoio para educadores. Ele utiliza IA para identificar possíveis indicadores de dificuldades de aprendizagem, com base em textos fornecidos. O objetivo é oferecer uma triagem inicial, e não um diagnóstico. A avaliação final deve sempre ser conduzida por um profissional qualificado.");

            predefinedResponses.put("Posso identificar indícios de Dislexia, Discalculia, dificuldades de interpretação, problemas na expressão escrita, déficit de atenção, entre outros.",
                    "Minha análise é focada em encontrar padrões textuais que possam sugerir dificuldades como:<br><br>" +
                            " - <strong>Dislexia e Disortografia:</strong> Troca de fonemas, inversão de sílabas.<br>" +
                            " - <strong>Interpretação:</strong> Dificuldade em extrair a ideia central do texto.<br>" +
                            " - <strong>Expressão Escrita:</strong> Falta de coesão e coerência.<br>" +
                            " - <strong>Discalculia:</strong> Problemas com raciocínio lógico-matemático em problemas escritos.<br>" +
                            " - <strong>Déficit de Atenção:</strong> Respostas desfocadas ou incompletas.");
        }

        public Optional<String> getResponseFor(String userInput) {
            return Optional.ofNullable(predefinedResponses.get(userInput));
        }
    }
}