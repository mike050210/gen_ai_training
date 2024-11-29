package com.epam.training.gen.ai.configuration;

/**
 * Class that holds the default Gen-AI settings.
 *
 * @param temperature      Determine randomness of responses, ranges from 0.0 to 1.0
 * @param frequencyPenalty Determine the penalty value of repeated tokens, ranges from 0.0 to 1.0
 * @param maxTokens        The maximum amount of tokens to handle
 */
public record GenAiSettings(
        double temperature,
        double frequencyPenalty,
        int maxTokens) {
}