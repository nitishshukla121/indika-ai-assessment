package com.example.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JacksonConfig {

    /**
     * This beans tells *every* RestTemplate that Spring Boot creates (including the one
     * inside Spring‑AI) to use an ObjectMapper that has JavaTimeModule.
     */
    @Bean
    public RestTemplateCustomizer javaTimeModuleCustomizer() {
        return restTemplate -> {
            restTemplate.getMessageConverters().stream()
                .filter(c -> c instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter)
                .map(c -> (org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) c)
                .findFirst()
                .ifPresent(c -> {
                    // Build a new ObjectMapper that already has the module
                    var mapper = Jackson2ObjectMapperBuilder.json()
                            .modules(new JavaTimeModule())
                            .build();
                    c.setObjectMapper(mapper);
                });
        };
    }
}
