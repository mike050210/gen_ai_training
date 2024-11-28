package com.epam.training.gen.ai.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Azure OpenAI Async Client bean.
 * <p>
 * This configuration defines a bean that provides an asynchronous client
 * for interacting with the Azure OpenAI Service. It uses the Azure Key
 * Credential for authentication and connects to a specified endpoint.
 */
@Configuration
@EnableConfigurationProperties(ClientOpenAiProperties.class)
@RequiredArgsConstructor
public class OpenAIConfiguration {
    private final ClientOpenAiProperties clientOpenAiProperties;

    /**
     * Creates an {@link OpenAIAsyncClient} bean for interacting with Azure OpenAI Service asynchronously.
     *
     * @return an instance of {@link OpenAIAsyncClient}
     */
    @Bean
    public OpenAIAsyncClient openAIAsyncClient() {
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(clientOpenAiProperties.clientOpenAiKey()))
                .endpoint(clientOpenAiProperties.clientOpenAiEndpoint())
                .buildAsyncClient();
    }
}
