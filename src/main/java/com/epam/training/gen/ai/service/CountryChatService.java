package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.epam.training.gen.ai.model.ChatbotResponse;
import com.epam.training.gen.ai.plugin.CountryPlugin;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.InvocationReturnMode;
import com.microsoft.semantickernel.orchestration.ToolCallBehavior;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

/**
 * Handles requests from Country info chat.
 */
@Service
public abstract class CountryChatService {
    private final OpenAIAsyncClient openAIAsyncClient;
    private final ChatHistory chatHistory;
    private final CountryService countryService;

    private InvocationContext invocationContext;

    private KernelPlugin countryPlugin;

    public CountryChatService(final ChatHistory chatHistory, final OpenAIAsyncClient openAIAsyncClient, final CountryService countryService) {
        this.chatHistory = chatHistory;
        this.openAIAsyncClient = openAIAsyncClient;
        this.countryService = countryService;

        this.invocationContext = new InvocationContext.Builder()
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();

        this.countryPlugin = KernelPluginFactory
                .createFromObject(new CountryPlugin(this.countryService), "CountryPlugin");
    }

    @Lookup
    protected abstract ChatCompletionService getChatCompletionService(OpenAIAsyncClient openAIAsyncClient,
                                                                      String deploymentName);

    @Lookup("pluginKernelBean")
    protected abstract Kernel getCountryKernel(ChatCompletionService chatCompletionService, KernelPlugin kernelPlugin);


    /**
     * Send a prompt with history to Azure OpenAI.
     *
     * @param userPrompt prompt send by user
     * @param model      AI Model to use
     * @return the response from the AI Assistant
     */
    public ChatbotResponse sendPromptWithHistory(String userPrompt, String model) {
        this.chatHistory.addUserMessage(userPrompt);

        ChatCompletionService chatCompletionService = getChatCompletionService(openAIAsyncClient, model);
        var response = chatCompletionService
                .getChatMessageContentsAsync(chatHistory,
                        getCountryKernel(chatCompletionService, countryPlugin),
                        invocationContext).block();

        chatHistory.addAssistantMessage(response.get(0).toString());

        return new ChatbotResponse(response.get(0).toString());
    }

    private KernelFunction<String> getKernelTemplate() {
        return KernelFunction.<String>createFromPrompt("""
                {{$chatHistory}}
                <message role="user">{{$userPrompt}}</message>""").build();
    }

    private KernelFunctionArguments getKernelFunctionArguments(ChatHistory chatHistory, String userPrompt) {
        return KernelFunctionArguments.builder()
                .withVariable("chatHistory", chatHistory)
                .withVariable("userPrompt", userPrompt)
                .build();
    }
}
