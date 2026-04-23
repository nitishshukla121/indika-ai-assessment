package com.example.controller;

import com.example.model.ChatResponse;
import com.example.service.ChatService;
import com.example.service.IngestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AppController {

    private final IngestionService ingestionService;
    private final ChatService chatService;

    public AppController(IngestionService ingestionService, ChatService chatService) {
        this.ingestionService = ingestionService;
        this.chatService = chatService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String status = ingestionService.processAndStoreFile(file);
            return ResponseEntity.ok(Map.of("message", status));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");
        ChatResponse response = chatService.askQuestion(question);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/summary")
    public ResponseEntity<Map<String, String>> generateSummary(@RequestBody Map<String, String> payload) {
        String documentName = payload.get("fileName");
        
        String prompt = "Please provide a detailed summary of the document named: " + documentName;
        ChatResponse response = chatService.askQuestion(prompt);
        
        return ResponseEntity.ok(Map.of("summary", response.getAnswer()));
    }
}