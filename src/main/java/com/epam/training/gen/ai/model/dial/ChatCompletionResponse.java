package com.epam.training.gen.ai.model.dial;

import java.util.List;

/**
 * Model to store the DIAL object response.
 *
 * @param id      request ID
 * @param object  Chat completion
 * @param created timestamp
 * @param choices object that contains the response
 */
public record ChatCompletionResponse(
        String id,
        String object,
        long created,
        List<Choice> choices) {
}

