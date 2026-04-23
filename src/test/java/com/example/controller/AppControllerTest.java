package com.example.controller;

import com.example.model.ChatResponse;
import com.example.service.ChatService;
import com.example.service.IngestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(AppController.class)
public class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngestionService ingestionService;

    @MockBean
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        Mockito.reset(ingestionService, chatService);
    }

    @Test
    void testUploadFile_Success() throws Exception {
        Mockito.when(ingestionService.processAndStoreFile(any())).thenReturn("File processed successfully!");

        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "test.pdf", 
                MediaType.APPLICATION_PDF_VALUE, 
                "Dummy PDF Content".getBytes()
        );

        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File processed successfully!"));
    }

    @Test
    void testUploadFile_Failure() throws Exception {
        Mockito.when(ingestionService.processAndStoreFile(any())).thenThrow(new RuntimeException("Upload failed"));

        MockMultipartFile file = new MockMultipartFile(
                "file", 
                "error.pdf", 
                MediaType.APPLICATION_PDF_VALUE, 
                "Dummy PDF Content".getBytes()
        );

        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("An internal error occurred: Upload failed"));
    }

    @Test
    void testChatEndpoint_Success() throws Exception {
        ChatResponse mockResponse = new ChatResponse("The summary is...", List.of("[01:15]"));
        Mockito.when(chatService.askQuestion(anyString())).thenReturn(mockResponse);

        String jsonPayload = "{\"question\": \"What is in the video?\"}";

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("The summary is..."));
    }

    @Test
    void testSummaryEndpoint_Success() throws Exception {
        ChatResponse mockResponse = new ChatResponse("This is a document summary.", List.of());
        Mockito.when(chatService.askQuestion(anyString())).thenReturn(mockResponse);

        String jsonPayload = "{\"fileName\": \"test.pdf\"}";

        mockMvc.perform(post("/api/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("This is a document summary."));
    }
}