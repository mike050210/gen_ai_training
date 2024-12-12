package com.epam.training.gen.ai.model.dial;

/**
 * Record that stores the Choice object from the DIAL response.
 *
 * @param index         value
 * @param message       object that stores the response
 * @param finish_reason why the process finishes
 */
public record Choice(
        int index,
        Message message,
        String finish_reason) {
}