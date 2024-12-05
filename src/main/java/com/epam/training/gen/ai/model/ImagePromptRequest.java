package com.epam.training.gen.ai.model;

import java.util.List;

/**
 * Record to handle the DIAL request.
 *
 * @param messages object to store the role and prompt
 */
public record ImagePromptRequest(List<ImageMessageRequest> messages) {
}
