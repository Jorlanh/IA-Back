package com.chatbot.model.dto.gemini;

import java.util.List;

public record GeminiRequest(List<Content> contents) {}