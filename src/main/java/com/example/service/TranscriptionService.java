package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class TranscriptionService {

    @Value("${openai.api-key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String transcribeAudio(File audioFile) {
        String url = "https://api.openai.com/v1/audio/transcriptions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(openAiApiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));
        body.add("model", "whisper-1");
        body.add("response_format", "verbose_json"); // CRITICAL: This tells Whisper to return timestamps
        body.add("timestamp_granularities[]", "segment");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            return response.getBody(); // Returns the JSON containing the text and timestamps
        } catch (Exception e) {
            throw new RuntimeException("Failed to transcribe audio with Whisper API: " + e.getMessage());
        }
    }
}