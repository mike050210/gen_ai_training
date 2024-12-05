package com.epam.training.gen.ai.model;

/**
 * Record to hold the user's prompt request
 *
 * @param input user's prompt
 * @param model AI model to use
 */
public record PromptRequest(String input, String model) {
}
