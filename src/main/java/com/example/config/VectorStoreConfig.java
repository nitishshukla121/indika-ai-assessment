package com.example.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.PineconeVectorStore;
import org.springframework.ai.vectorstore.PineconeVectorStore.PineconeVectorStoreConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorStoreConfig {

    @Value("${spring.ai.vectorstore.pinecone.api-key}")
    private String pineconeApiKey;

    @Value("${spring.ai.vectorstore.pinecone.environment:gcp-starter}")
    private String pineconeEnvironment;

    @Value("${spring.ai.vectorstore.pinecone.project-id:}")
    private String pineconeProjectId;

    @Value("${spring.ai.vectorstore.pinecone.index-name}")
    private String pineconeIndex;

    @Bean
    public PineconeVectorStoreConfig pineconeVectorStoreConfig() {
        return PineconeVectorStoreConfig.builder()
                .withApiKey(pineconeApiKey)
                .withEnvironment(pineconeEnvironment)
                .withProjectId(pineconeProjectId)
                .withIndexName(pineconeIndex)
                .build();
    }

    @Bean
    public VectorStore vectorStore(PineconeVectorStoreConfig config, EmbeddingModel embeddingModel) {
        return new PineconeVectorStore(config, embeddingModel);
    }
}