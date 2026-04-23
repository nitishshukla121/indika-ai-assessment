package com.example.service;

import com.example.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public ChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public ChatResponse askQuestion(String question) {
        // 1. Retrieve relevant context from Pinecone
        List<Document> similarDocuments = vectorStore.similaritySearch(SearchRequest.query(question).withTopK(3));
        String context = similarDocuments.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n"));

        // 2. Create the Prompt specifically designed to handle timestamps
        String promptString = """
                You are a helpful AI assistant analyzing document and multimedia transcripts.
                Use the following context to answer the user's question.
                If the context contains timestamps (e.g., [01:23]), include those exact timestamps in your answer.
                
                Context:
                {context}
                
                Question:
                {question}
                """;

        PromptTemplate template = new PromptTemplate(promptString);
        template.add("context", context);
        template.add("question", question);

        // 3. Call Gemini
        String answer = chatClient.prompt(template.create()).call().content();

        // 4. Return response (Timestamps parsing logic can be expanded here based on regex)
        return new ChatResponse(answer, List.of()); 
    }
}