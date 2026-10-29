package com.ai.courseassistant;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunker {

    private static final int CHUNK_SIZE = 1000;
    private static final int OVERLAP = 150;

    public List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();

        text = text.replaceAll("\\s+", " ").trim();

        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + CHUNK_SIZE, text.length());

            if (end < text.length()) {
                int boundary = text.lastIndexOf(". ", end);

                if (boundary > start + CHUNK_SIZE / 2) {
                    end = boundary + 1;
                }
            }

            String chunk = text.substring(start, end).trim();

            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            if (end == text.length()) {
                break;
            }

            start = Math.max(start + 1, end - OVERLAP);
        }

        return chunks;
    }
}