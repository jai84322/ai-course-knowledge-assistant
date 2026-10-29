package com.ai.courseassistant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DocumentChunkRepository {

    private final JdbcTemplate jdbcTemplate;

    public DocumentChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(String documentName, String content, float[] embedding) {
        String vector = toVectorString(embedding);

        String sql = """
                INSERT INTO document_chunks
                    (document_name, content, embedding)
                VALUES (?, ?, ?::vector)
                """;

        jdbcTemplate.update(sql, documentName, content, vector);
    }

    private String toVectorString(float[] embedding) {
        StringBuilder vector = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                vector.append(",");
            }
            vector.append(embedding[i]);
        }
        return vector.append("]").toString();
    }

    public List<Map<String, Object>> searchSimilar(float[] embedding, int limit) {
        String vector = toVectorString(embedding);

        String sql = """
            SELECT id, document_name, content,
                   1 - (embedding <=> ?::vector) AS similarity
            FROM document_chunks
            ORDER BY embedding <=> ?::vector
            LIMIT ?
            """;

        return jdbcTemplate.queryForList(sql, vector, vector, limit);
    }
}