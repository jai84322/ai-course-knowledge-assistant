package com.ai.courseassistant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping("/ask")
    public String ask(@RequestBody String question) {
        System.out.println("1. Request received: " + question);

        String response = chatClient.prompt()
                .user(question)
                .call()
                .content();

        System.out.println("2. Gemini responded");
        return response;
    }
}