package com.example.service;

import com.example.model.FileMetadata;
import com.example.repository.FileMetadataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class IngestionService {

    private final VectorStore vectorStore;
    private final FileMetadataRepository metadataRepository;
    private final TranscriptionService transcriptionService;
    private final ObjectMapper objectMapper;

    public IngestionService(VectorStore vectorStore,
                            FileMetadataRepository metadataRepository,
                            TranscriptionService transcriptionService,
                            ObjectMapper objectMapper) {
        this.vectorStore = vectorStore;
        this.metadataRepository = metadataRepository;
        this.transcriptionService = transcriptionService;
        this.objectMapper = objectMapper;
    }

    public FileMetadata processAndStoreFile(MultipartFile file) throws Exception {
        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setFileType(file.getContentType());
        metadata.setFileSize(file.getSize());
        // uploadDate removed

        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }

        try {
            String mimeType = file.getContentType();
            String extractedText;

            if (mimeType != null && (mimeType.startsWith("audio/") || mimeType.startsWith("video/"))) {
                TranscriptionService.TranscriptionResult result = transcriptionService.transcribe(tempFile);
                extractedText = result.text();
                metadata.setTranscription(extractedText);
                metadata.setSegmentsJson(objectMapper.writeValueAsString(result.segments()));
            } else {
                TikaDocumentReader reader = new TikaDocumentReader(new FileSystemResource(tempFile));
                List<Document> docs = reader.get();
                extractedText = docs.stream().map(Document::getText).reduce("", String::concat);
                metadata.setTranscription(extractedText);
            }

            metadataRepository.save(metadata);

            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> chunks = splitter.apply(List.of(new Document(extractedText)));
            vectorStore.accept(chunks);

        } finally {
            tempFile.delete();
        }

        return metadata;
    }
}