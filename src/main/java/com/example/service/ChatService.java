package com.example.service;

import com.example.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    @Cacheable(value = "chatAnswers", key = "#question")
    public ChatResponse askQuestion(String question) {
        List<Document> similarDocuments = vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(question)
                .topK(5)
                .build()
        );

        String context = similarDocuments.stream()
            .map(Document::getText)
            .collect(Collectors.joining("\n"));

        String promptString = """
            You are a helpful AI assistant analyzing documents and multimedia transcripts.
            Use the following context to answer the user's question.
            If the context contains timestamps in seconds (e.g., at 12.5 seconds) or time markers, include them in your answer.

            Context:
            {context}

            Question:
            {question}

            Answer:
            """;

        PromptTemplate template = new PromptTemplate(promptString);
        template.add("context", context);
        template.add("question", question);

        String answer = chatClient.prompt(template.create()).call().content();
        List<String> timestamps = extractTimestamps(context);

        return new ChatResponse(answer, timestamps);
    }

    @Cacheable(value = "summaries", key = "#fileId")
    public String summarize(Long fileId, String content) {
        String prompt = "Summarize the following content in 3-4 sentences:\n\n" + content;
        return chatClient.prompt().user(prompt).call().content();
    }

    private List<String> extractTimestamps(String context) {
        List<String> timestamps = new ArrayList<>();
        // Match patterns like 12.5, 0:45, 1:23:45
        Pattern pattern = Pattern.compile("\\b(\\d{1,2}:\\d{2}(?::\\d{2})?|\\d+\\.\\d+)\\b");
        Matcher matcher = pattern.matcher(context);
        while (matcher.find()) {
            String ts = matcher.group(1);
            if (!timestamps.contains(ts)) timestamps.add(ts);
        }
        return timestamps;
    }
}