package com.epam.training.gen.ai.model;

/**
 * API Model to store the string to get the embeddings.
 *
 * @param text to create vectors from
 */
public record EmbeddingRequest(String text) {
}
