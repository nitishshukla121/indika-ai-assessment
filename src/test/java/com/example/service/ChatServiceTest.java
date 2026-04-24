package com.example.service;

import com.example.model.ChatResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock VectorStore vectorStore;
    @Mock ChatClient.Builder chatClientBuilder;
    @Mock ChatClient chatClient;
    @Mock ChatClient.ChatClientRequestSpec requestSpec;
    @Mock ChatClient.CallResponseSpec callSpec;

    @Test
    void askQuestion_returnsAnswerWithTimestamps() {
        Document doc = new Document("At 12.5 seconds the speaker says hello");
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc));
        when(chatClientBuilder.build()).thenReturn(chatClient);
        // doReturn avoids ambiguous overload on prompt()
        doReturn(requestSpec).when(chatClient).prompt(any(Prompt.class));
        when(requestSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn("The speaker says hello at 12.5 seconds");

        ChatService service = new ChatService(chatClientBuilder, vectorStore);
        ChatResponse response = service.askQuestion("What does the speaker say?");

        assertThat(response.getAnswer()).contains("hello");
        assertThat(response.getRelevantTimestamps()).contains("12.5");
    }

    @Test
    void askQuestion_emptyContext_returnsAnswer() {
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());
        when(chatClientBuilder.build()).thenReturn(chatClient);
        doReturn(requestSpec).when(chatClient).prompt(any(Prompt.class));
        when(requestSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn("I don't have enough context.");

        ChatService service = new ChatService(chatClientBuilder, vectorStore);
        ChatResponse response = service.askQuestion("Random question");

        assertThat(response.getAnswer()).isNotNull();
        assertThat(response.getRelevantTimestamps()).isEmpty();
    }

    @Test
    void summarize_returnsSummaryText() {
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn("This is a summary.");

        ChatService service = new ChatService(chatClientBuilder, vectorStore);
        String summary = service.summarize(1L, "Long document content here");

        assertThat(summary).isEqualTo("This is a summary.");
    }
}