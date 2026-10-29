package com.ai.courseassistant;

import com.ai.courseassistant.PdfService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class PdfController {

    private final PdfService pdfService;
    private final TextChunker textChunker;
    private final EmbeddingService embeddingService;
    private final DocumentChunkRepository documentChunkRepository;

    public PdfController(PdfService pdfService, TextChunker textChunker, EmbeddingService embeddingService, DocumentChunkRepository documentChunkRepository) {
        this.pdfService = pdfService;
        this.textChunker = textChunker;
        this.embeddingService = embeddingService;
        this.documentChunkRepository = documentChunkRepository;
    }

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file)
            throws IOException {
        return pdfService.extractText(file);
    }

    @PostMapping("/upload/chunks")
    public List<String> uploadAndChunkPdf(
            @RequestParam("file") MultipartFile file) throws IOException {

        String text = pdfService.extractText(file);
        return textChunker.chunkText(text);
    }

    @PostMapping("/upload/embeddings")
    public List<float[]>  uploadAndEmbedPdf(
            @RequestParam("file") MultipartFile file) throws IOException {

        String text = pdfService.extractText(file);
        List<String> chunks = textChunker.chunkText(text);

        return chunks.stream()
                .map(embeddingService::generateEmbedding)
                .toList();
    }

    @PostMapping("/upload/store")
    public String uploadAndStorePdf(
            @RequestParam("file") MultipartFile file) throws IOException {

        String text = pdfService.extractText(file);
        List<String> chunks = textChunker.chunkText(text);

        for (String chunk : chunks) {
            float[] embedding = embeddingService.generateEmbedding(chunk);

            documentChunkRepository.save(
                    file.getOriginalFilename(),
                    chunk,
                    embedding
            );
        }

        return "Stored " + chunks.size() + " chunks successfully";
    }
}