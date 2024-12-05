package com.epam.training.gen.ai.model;

/**
 * Record that contains the role and prompt to send to DIAL.
 *
 * @param role    set to "user"
 * @param content user prompt
 */
public record ImageMessageRequest(String role, String content) {
}
