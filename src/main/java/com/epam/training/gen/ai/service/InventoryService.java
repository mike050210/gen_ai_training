package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.epam.training.gen.ai.model.ChatbotResponse;
import com.epam.training.gen.ai.model.InventoryItem;
import com.epam.training.gen.ai.plugin.InventoryPlugin;
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

import java.util.Map;

/**
 * Handles requests for inventory management.
 */
@Service
public abstract class InventoryService {
    private final ChatHistory chatHistory;
    private final OpenAIAsyncClient openAIAsyncClient;

    private InvocationContext invocationContext;

    private KernelPlugin inventoryPlugin;

    public InventoryService(
            final ChatHistory chatHistory,
            final OpenAIAsyncClient openAIAsyncClient,
            final Map<Integer, InventoryItem> inventory) {
        this.chatHistory = chatHistory;
        this.openAIAsyncClient = openAIAsyncClient;

        this.invocationContext = new InvocationContext.Builder()
                .withReturnMode(InvocationReturnMode.LAST_MESSAGE_ONLY)
                .withToolCallBehavior(ToolCallBehavior.allowAllKernelFunctions(true))
                .build();

        this.inventoryPlugin = KernelPluginFactory
                .createFromObject(new InventoryPlugin(inventory), "InventoryPlugin");
    }

    @Lookup
    protected abstract ChatCompletionService getChatCompletionService(OpenAIAsyncClient openAIAsyncClient,
                                                                      String deploymentName);

    @Lookup("pluginKernelBean")
    protected abstract Kernel getInventoryKernel(ChatCompletionService chatCompletionService, KernelPlugin kernelPlugin);


    /**
     * Send a prompt with history to Azure OpenAI to handle inventory.
     *
     * @param userPrompt prompt send by user
     * @param model      AI Model to use
     * @return the response from the AI Assistant
     */
    public ChatbotResponse sendPromptWithHistory(String userPrompt, String model) {
        this.chatHistory.addSystemMessage("You are a chat-bot capable of handling a store inventory. You can add or remove units from inventory items.");
        this.chatHistory.addUserMessage(userPrompt);

        ChatCompletionService chatCompletionService = getChatCompletionService(openAIAsyncClient, model);
        var response = chatCompletionService
                .getChatMessageContentsAsync(chatHistory,
                        getInventoryKernel(chatCompletionService, inventoryPlugin),
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
