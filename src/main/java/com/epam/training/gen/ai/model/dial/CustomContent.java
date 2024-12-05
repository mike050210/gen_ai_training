package com.epam.training.gen.ai.model.dial;

import java.util.List;

/**
 * Record that stores the attachment list from the DIAL response.
 *
 * @param attachments Attachement List object
 */
public record CustomContent(List<Attachment> attachments) {
}

