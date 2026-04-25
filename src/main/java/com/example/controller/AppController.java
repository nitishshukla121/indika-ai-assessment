package com.example.controller;

import com.example.model.FileMetadata;
import com.example.repository.FileMetadataRepository;
import com.example.service.ChatService;
import com.example.service.IngestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.model.ChatResponse;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
public class AppController {

    private final IngestionService ingestionService;
    private final ChatService chatService;
    private final FileMetadataRepository fileMetadataRepository;
    private final ObjectMapper objectMapper;

    public AppController(IngestionService ingestionService,
                         ChatService chatService,
                         FileMetadataRepository fileMetadataRepository,
                         ObjectMapper objectMapper) {
        this.ingestionService = ingestionService;
        this.chatService = chatService;
        this.fileMetadataRepository = fileMetadataRepository;
        this.objectMapper = objectMapper;
    }

    // Upload PDF, audio, or video
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FileMetadata metadata = ingestionService.processAndStoreFile(file);
            Map<String, Object> response = new HashMap<>();
            response.put("id", metadata.getId());
            response.put("fileName", metadata.getFileName());
            response.put("fileType", metadata.getFileType());
            response.put("fileSize", metadata.getFileSize());
            response.put("hasTranscription", metadata.getTranscription() != null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    // List all uploaded files
    @GetMapping("/files")
    public ResponseEntity<List<FileMetadata>> listFiles() {
        return ResponseEntity.ok(fileMetadataRepository.findAll());
    }

    // Delete a file
    @DeleteMapping("/files/{id}")
    public ResponseEntity<?> deleteFile(@PathVariable Long id) {
        if (!fileMetadataRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        fileMetadataRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "File deleted"));
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        System.out.println("DEBUG: Received question -> " + question); // 👈 Ye line dalo
        ChatResponse response = chatService.askQuestion(question);
        return ResponseEntity.ok(response);
    }

    // Get transcript segments with timestamps for a media file
    @GetMapping("/media/{id}/segments")
    public ResponseEntity<?> getSegments(@PathVariable Long id) {
        return fileMetadataRepository.findById(id).map(meta -> {
            try {
                if (meta.getSegmentsJson() == null) {
                    return ResponseEntity.ok(Map.of("segments", List.of(), "transcript", ""));
                }
                Object segments = objectMapper.readValue(meta.getSegmentsJson(), Object.class);
                return ResponseEntity.ok(Map.of(
                    "segments", segments,
                    "transcript", meta.getTranscription() != null ? meta.getTranscription() : ""
                ));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    // Generate or return summary for a file
    @GetMapping("/summarize/{id}")
    public ResponseEntity<?> summarize(@PathVariable Long id) {
        return fileMetadataRepository.findById(id).map(meta -> {
            if (meta.getSummary() != null && !meta.getSummary().isBlank()) {
                return ResponseEntity.ok(Map.of("summary", meta.getSummary()));
            }
            String content = meta.getTranscription() != null ? meta.getTranscription() : "";
            if (content.isBlank()) {
                return ResponseEntity.ok(Map.of("summary", "No content available to summarize."));
            }
            String summary = chatService.summarize(id, content);
            meta.setSummary(summary);
            fileMetadataRepository.save(meta);
            return ResponseEntity.ok(Map.of("summary", summary));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Health check
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}