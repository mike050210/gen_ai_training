package com.epam.training.gen.ai.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Record to load the Client OpenAI properties.
 */
@ConfigurationProperties()
public record ClientOpenAiProperties(
        String clientOpenAiKey,
        String clientOpenAiEndpoint,
        String clientOpenAiDeploymentName,
        String clientOpenAiText2ImageModel,
        String clientOpenAiEmbeddingModel,
        GenAiSettings genAi,
        ApplicationProperties application) {
}
