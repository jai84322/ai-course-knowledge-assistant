package com.ai.courseassistant;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionAnswerService {

    private final ChatClient chatClient;

    public QuestionAnswerService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generateAnswer(
            String question,
            List<Map<String, Object>> sources) {

        String context = sources.stream()
                .map(source -> source.get("content").toString())
                .collect(Collectors.joining("\n\n---\n\n"));

        return chatClient.prompt()
                .system("""
                    You are a course knowledge assistant.
                    Answer the user's question using only the
                    provided context.
                    If the context does not contain the answer,
                    say that you don't have enough information.
                    Do not invent facts.
                    """)
                .user("""
                    Context:
                    %s

                    Question:
                    %s
                    """.formatted(context, question))
                .call()
                .content();
    }
}