package com.chatbot.model.controller;

import com.chatbot.model.dto.HelenaResponse;
import com.chatbot.model.dto.UserInput;
import com.chatbot.model.service.HelenaChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://ia-front-i75b.onrender.com") // Permite acesso do frontend React
public class ChatController {

    private final HelenaChatService chatService;

    @PostMapping("/chat")
    public HelenaResponse handleChat(@RequestBody UserInput userInput) {
        return chatService.getResponse(userInput);
    }
}