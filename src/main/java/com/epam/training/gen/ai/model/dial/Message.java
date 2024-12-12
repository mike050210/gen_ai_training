package com.epam.training.gen.ai.model.dial;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Record that stores the message object from the DIAL response.
 *
 * @param role          assistant role
 * @param content       object
 * @param customContent object that contains the image
 */
public record Message(
        String role,
        String content,
        @JsonProperty("custom_content")
        CustomContent customContent) {
}