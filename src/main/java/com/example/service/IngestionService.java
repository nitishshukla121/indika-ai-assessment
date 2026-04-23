package com.example.service;

import com.example.model.FileMetadata;
import com.example.repository.FileMetadataRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class IngestionService {

    private final VectorStore vectorStore;
    private final FileMetadataRepository metadataRepository;
 // Add TranscriptionService to your constructor injection
    private final TranscriptionService transcriptionService;

    public IngestionService(VectorStore vectorStore, FileMetadataRepository metadataRepository, TranscriptionService transcriptionService) {
        this.vectorStore = vectorStore;
        this.metadataRepository = metadataRepository;
        this.transcriptionService = transcriptionService;
    }

    public String processAndStoreFile(MultipartFile file) throws IOException {
        // 1. Save metadata
        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setFileType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadataRepository.save(metadata);

        // 2. Save file temporarily
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }

        String extractedText = "";

        // 3. Route based on file type
        String mimeType = file.getContentType();
        if (mimeType != null && (mimeType.startsWith("audio/") || mimeType.startsWith("video/"))) {
            // Use Whisper for Media
            extractedText = transcriptionService.transcribeAudio(tempFile);
        } else {
            // Use Tika for PDFs/Text
            TikaDocumentReader reader = new TikaDocumentReader(new org.springframework.core.io.FileSystemResource(tempFile));
            List<Document> documents = reader.get();
            extractedText = documents.stream().map(Document::getContent).reduce("", String::concat);
        }

        // 4. Chunk and embed the extracted text
        TokenTextSplitter textSplitter = new TokenTextSplitter();
        List<Document> chunkedDocuments = textSplitter.apply(List.of(new Document(extractedText)));
        vectorStore.accept(chunkedDocuments);

        tempFile.delete();
        return "File processed successfully!";
    }
}