package com.example.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Add the JavaTimeModule to every RestTemplate that Spring Boot builds.
     */
    @Bean
    public RestTemplateCustomizer javaTimeModuleCustomizer() {
        return restTemplate -> restTemplate.getMessageConverters()
            .stream()
            .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
            .map(c -> (MappingJackson2HttpMessageConverter) c)
            .findFirst()
            .ifPresent(c -> {
                // Use a shared ObjectMapper that already has the module
                c.setObjectMapper(
                        Jackson2ObjectMapperBuilder.json()
                                .modules(new JavaTimeModule())
                                .build());
            });
    }

    // If you prefer, you can return a new RestTemplateBuilderCustomizer
    // that registers the module for all builders – the idea is the same.
}
