package com.epam.training.gen.ai.model.dial;

/**
 * Model to store the Attachment object from the DIAL response.
 *
 * @param title Image
 * @param type  image/png
 * @param url   that holds the image
 */
public record Attachment(
        String title,
        String type,
        String url) {

}
