package com.epam.training.gen.ai.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up Semantic Kernel beans.
 * <p>
 * This configuration provides several beans necessary for the interaction with Azure OpenAI services.
 * It defines beans for chat completion services, kernel instance, and invocation context settings.
 */
@Configuration
@RequiredArgsConstructor
public class SemanticKernelConfiguration {

    private final ClientOpenAiProperties clientOpenAiProperties;

    /**
     * Generates a {@link ChatCompletionService} bean for handling chat completions.
     *
     * @param openAIAsyncClient to communicate with Azure OpenAI
     * @return an instance of {@link ChatCompletionService}
     */
    @Bean
    public ChatCompletionService chatCompletionService(OpenAIAsyncClient openAIAsyncClient) {
        return OpenAIChatCompletion.builder()
                .withModelId(clientOpenAiProperties.clientOpenAiDeploymentName())
                .withOpenAIAsyncClient(openAIAsyncClient)
                .build();
    }

    /**
     * Generates a {@link Kernel} bean to manage AI services.
     *
     * @param chatCompletionService to use as service
     * @return the {@link Kernel} instance
     */
    @Bean
    public Kernel kernel(ChatCompletionService chatCompletionService) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletionService)
                .build();
    }

    /**
     * Generates a {@link InvocationContext} bean that adds additional settings like the Temperature or the model.
     *
     * @return an {@link InvocationContext} instance
     */
    @Bean
    public InvocationContext invocationContext() {
        return InvocationContext.builder()
                .withPromptExecutionSettings(PromptExecutionSettings.builder()
                        .withTemperature(clientOpenAiProperties.genAi().temperature())
                        .withMaxTokens(clientOpenAiProperties.genAi().maxTokens())
                        .withFrequencyPenalty(clientOpenAiProperties.genAi().frequencyPenalty())
                        .withModelId(clientOpenAiProperties.clientOpenAiDeploymentName())
                        .build())
                .build();
    }
}
