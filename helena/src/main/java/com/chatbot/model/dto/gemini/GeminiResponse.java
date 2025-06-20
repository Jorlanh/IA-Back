package com.chatbot.model.dto.gemini;

import java.util.List;

public record GeminiResponse(List<Candidate> candidates) {}