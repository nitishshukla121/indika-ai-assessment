package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class TranscriptionService {

    @Value("${openai.api-key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TranscriptionService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public TranscriptionResult transcribe(File audioFile) {
        String url = "https://api.openai.com/v1/audio/transcriptions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(openAiApiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(audioFile));
        body.add("model", "whisper-1");
        body.add("response_format", "verbose_json");
        body.add("timestamp_granularities[]", "segment");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
            return parseResponse(response.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Whisper transcription failed: " + e.getMessage(), e);
        }
    }

    private TranscriptionResult parseResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            String text = root.path("text").asText();

            List<TranscriptSegment> segments = new ArrayList<>();
            JsonNode segs = root.path("segments");
            if (segs.isArray()) {
                for (JsonNode seg : segs) {
                    segments.add(new TranscriptSegment(
                        seg.path("start").asDouble(),
                        seg.path("end").asDouble(),
                        seg.path("text").asText().trim()
                    ));
                }
            }
            return new TranscriptionResult(text, segments);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Whisper response: " + e.getMessage(), e);
        }
    }

    // Legacy method for backward compat
    public String transcribeAudio(File audioFile) {
        return transcribe(audioFile).text();
    }

    public record TranscriptionResult(String text, List<TranscriptSegment> segments) {}
    public record TranscriptSegment(double start, double end, String text) {}
}