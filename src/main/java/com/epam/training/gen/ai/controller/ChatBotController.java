package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.ChatbotResponse;
import com.epam.training.gen.ai.model.PromptRequest;
import com.epam.training.gen.ai.service.PromptService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST Controller to send prompt to the Azure OpenAI Assitant and get responses.
 */
@RestController
@RequestMapping(path = "/api/chat-bot", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ChatBotController {

    private final PromptService promptService;

    /**
     * Simple endpoint to send Azure OpenAI a user prompt.
     *
     * @param request the user's prompt
     * @return the OpenAI response
     */
    @PostMapping(value = "/simple-prompt")
    public ChatbotResponse sendSimplePrompt(@RequestBody PromptRequest request) {
        validateInput(request.input());
        return promptService.sendSimplePrompt(request.input(), request.model());
    }

    /**
     * Eendpoint to send Azure OpenAI a user prompt with history.
     *
     * @param request the user's prompt
     * @return the OpenAI response
     */
    @PostMapping(value = "/prompt-with-history")
    public ChatbotResponse sendPromptWithHistory(@RequestBody PromptRequest request) {
        validateInput(request.input());
        return promptService.sendPromptWithHistory(request.input(), request.model());
    }

    private void validateInput(String input) {
        if (StringUtils.isEmpty(input)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'input' request parameter is mandatory");
        }
    }
}
