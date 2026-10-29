package com.ai.courseassistant;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final EmbeddingService embeddingService;
    private final DocumentChunkRepository repository;
    private final QuestionAnswerService questionAnswerService;

    public QuestionController(
            EmbeddingService embeddingService,
            DocumentChunkRepository repository,
            QuestionAnswerService questionAnswerService) {
        this.embeddingService = embeddingService;
        this.repository = repository;
        this.questionAnswerService = questionAnswerService;
    }

    @PostMapping
    public Map<String, Object> ask(@RequestBody Map<String, String> request) {

        String question = request.get("question");

        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question is required");
        }

        float[] embedding = embeddingService.generateEmbedding(question);
        List<Map<String, Object>> sources = repository.searchSimilar(embedding, 3);
        String answer = questionAnswerService.generateAnswer(question, sources);

        return Map.of(
                "question", question,
                "answer", answer,
                "sources", sources
        );
    }
}