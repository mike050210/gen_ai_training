package com.epam.training.gen.ai.model;

/**
 * Model to store the API embeddings response
 */
public record EmbeddingSearchItem(String id, float score, String payload) {
}
