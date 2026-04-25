package com.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptionServiceTest {

    @Mock RestTemplate restTemplate;

    TranscriptionService transcriptionService;

    @BeforeEach
    void setUp() {
        transcriptionService = new TranscriptionService(restTemplate, new ObjectMapper());
        // field is named "apiKey" in the service
        ReflectionTestUtils.setField(transcriptionService, "apiKey", "test-key");
    }

    @Test
    void transcribe_parsesTextAndSegments() throws IOException {
        String fakeResponse = """
            {
              "text": "Hello world this is a test",
              "segments": [
                {"start": 0.0, "end": 2.5, "text": "Hello world"},
                {"start": 2.5, "end": 5.0, "text": "this is a test"}
              ]
            }
            """;
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok(fakeResponse));

        File tempFile = Files.createTempFile("test", ".mp3").toFile();
        tempFile.deleteOnExit();

        TranscriptionService.TranscriptionResult result = transcriptionService.transcribe(tempFile);

        assertThat(result.text()).isEqualTo("Hello world this is a test");
        assertThat(result.segments()).hasSize(2);
        assertThat(result.segments().get(0).start()).isEqualTo(0.0);
        assertThat(result.segments().get(0).text()).isEqualTo("Hello world");
        assertThat(result.segments().get(1).end()).isEqualTo(5.0);
    }

    @Test
    void transcribeAudio_returnsTextOnly() throws IOException {
        String fakeResponse = """
            {"text": "Simple transcript", "segments": []}
            """;
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok(fakeResponse));

        File tempFile = Files.createTempFile("test", ".mp3").toFile();
        tempFile.deleteOnExit();

        String text = transcriptionService.transcribeAudio(tempFile);
        assertThat(text).isEqualTo("Simple transcript");
    }

    @Test
    void transcribe_onApiError_returnsFallback() throws IOException {
        // Service catches errors and returns fallback — does NOT throw
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenThrow(new RuntimeException("401 Unauthorized"));

        File tempFile = Files.createTempFile("test", ".mp3").toFile();
        tempFile.deleteOnExit();

        TranscriptionService.TranscriptionResult result = transcriptionService.transcribe(tempFile);
        assertThat(result.text()).contains("unavailable");
        assertThat(result.segments()).isEmpty();
    }

    @Test
    void transcribe_emptySegments_returnsEmptyList() throws IOException {
        String fakeResponse = """
            {"text": "No segments here", "segments": []}
            """;
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
            .thenReturn(ResponseEntity.ok(fakeResponse));

        File tempFile = Files.createTempFile("test", ".mp3").toFile();
        tempFile.deleteOnExit();

        TranscriptionService.TranscriptionResult result = transcriptionService.transcribe(tempFile);
        assertThat(result.segments()).isEmpty();
        assertThat(result.text()).isEqualTo("No segments here");
    }

    @Test
    void transcriptSegment_record_works() {
        var seg = new TranscriptionService.TranscriptSegment(1.0, 3.5, "test text");
        assertThat(seg.start()).isEqualTo(1.0);
        assertThat(seg.end()).isEqualTo(3.5);
        assertThat(seg.text()).isEqualTo("test text");
    }
}