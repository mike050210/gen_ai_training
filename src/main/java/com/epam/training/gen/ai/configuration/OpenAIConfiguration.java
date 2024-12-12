package com.epam.training.gen.ai.configuration;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import com.epam.training.gen.ai.model.InventoryItem;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * Creates a {@link WebClient} bean for EPAM Dial communication.
     *
     * @return an instance of {@link WebClient}
     */
    @Bean("dialWebClient")
    @Qualifier("dialWebClient")
    public WebClient dialWebClient() {
        return WebClient.builder()
                .baseUrl(clientOpenAiProperties.clientOpenAiEndpoint())
                .defaultHeader("Api-Key", clientOpenAiProperties.clientOpenAiKey())
                .build();
    }

    /**
     * Creates a {@link WebClient.RequestBodySpec} bean for EPAM Dial communication based on model.
     *
     * @return an instance of {@link WebClient.RequestBodySpec}
     */
    @Bean("dialBodySpec")
    @Qualifier("dialBodySpec")
    public WebClient.RequestBodySpec dialRequestBodySpec(@Qualifier("dialWebClient") WebClient webClient) {
        String endpoint = String.format(clientOpenAiProperties.application().chatCompletionsUrl(),
                clientOpenAiProperties.clientOpenAiText2ImageModel());
        return webClient.post().uri(endpoint);
    }

    /**
     * Creates a {@link WebClient} bean for Country API communication.
     *
     * @return an instance of {@link WebClient}
     */
    @Bean("countryWebClient")
    @Qualifier("countryWebClient")
    public WebClient countryWebclient() {
        return WebClient.builder()
                .baseUrl(clientOpenAiProperties.application().countriesApiUrl())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(codec -> codec.defaultCodecs().maxInMemorySize(1 * 1024 * 1024))
                        .build())
                .build();
    }


    /**
     * Creates a {@link ChatHistory} instance of session scope that would store the Prompt History so that the AI
     * Assistant get the previous context of the conversation.
     *
     * @return a {@link ChatHistory} bean
     */
    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public ChatHistory chatHistory() {
        return new ChatHistory();
    }


    /**
     * Creates the initial inventory mock instance.
     *
     * @return a Map with all current items in the inventory
     */
    @Bean
    public Map<Integer, InventoryItem> inventoryItemMap() {
        Map<Integer, InventoryItem> inventory = new HashMap<>();
        inventory.put(1, new InventoryItem(1, "Laptop HP", 10, 50));
        inventory.put(2, new InventoryItem(1, "Mouse Logitech", 100, 200));
        inventory.put(3, new InventoryItem(1, "Monitor Asus", 15, 40));
        return inventory;
    }
}
