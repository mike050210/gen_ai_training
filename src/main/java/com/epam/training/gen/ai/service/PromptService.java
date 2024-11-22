package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.ChatbotResponse;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromptService {
    private final ChatCompletionService chatCompletionService;
    private final Kernel kernel;
    private final InvocationContext invocationContext;

    /**
     * Send a simple prompt to Azure OpenAI.
     *
     * @param userPrompt prompt send by user
     * @return the response from the AI Assistant
     */
    public ChatbotResponse sendSimplePrompt(String userPrompt) {
        var chatHistory = new ChatHistory();
        chatHistory.addUserMessage(userPrompt);

        var response = chatCompletionService.getChatMessageContentsAsync(chatHistory, kernel, invocationContext).block();
        var message = response.stream()
                .map(messageContent -> messageContent.getContent())
                .collect(Collectors.joining("\n"));

        return new ChatbotResponse(message);
    }

    /**
     * Send a prompt with history to Azure OpenAI.
     *
     * @param userPrompt prompt send by user
     * @return the response from the AI Assistant
     */
    public ChatbotResponse sendPromptWithHistory(String userPrompt) {
        var chatHistory = new ChatHistory();
        var response = kernel.invokeAsync(getKernelTemplate())
                .withArguments(getKernelFunctionArguments(chatHistory, userPrompt)).block();

        // Add messages to history
        chatHistory.addUserMessage(userPrompt);
        chatHistory.addAssistantMessage(response.getResult());

        return new ChatbotResponse(response.getResult());
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
