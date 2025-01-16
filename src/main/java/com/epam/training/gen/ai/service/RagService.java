package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.configuration.ClientOpenAiProperties;
import com.epam.training.gen.ai.model.EmbeddingSearchItem;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to control the RAG transactions.
 */
@Slf4j
@Service
public class RagService {

    private static Double RAG_MIN_SCORE = 0.7;
    private static int MAX_DOCUMENT_SEGMENT_SIZE = 100;
    private static int MAX_OVERLAP_SIZE_IN_TOKENS = 100;
    private static String TOKENIZER_MODEL = "gpt-3.5-turbo";

    private EmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;

    /**
     * Constructor.
     *
     * @param qdrantClient           injected Qdrant client
     * @param clientOpenAiProperties injected app properties
     */
    public RagService(final QdrantClient qdrantClient, final ClientOpenAiProperties clientOpenAiProperties) {
        this.embeddingStore = QdrantEmbeddingStore.builder()
                .client(qdrantClient)
                .collectionName(clientOpenAiProperties.application().db().ragCollection())
                .build();
        this.embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    }

    /**
     * Store the document text into segments to the RAG collection.
     *
     * @param documentText the document to store
     */
    public void storeDocument(String documentText) {
        List<TextSegment> segments = segmentDocumentText(documentText);
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        embeddingStore.addAll(embeddings, segments);
    }

    private List<TextSegment> segmentDocumentText(String documentText) {
        Document document = Document.document(documentText);
        DocumentSplitter splitter = DocumentSplitters.recursive(
                MAX_DOCUMENT_SEGMENT_SIZE,
                MAX_OVERLAP_SIZE_IN_TOKENS,
                new OpenAiTokenizer(TOKENIZER_MODEL));
        List<TextSegment> segments = splitter.split(document);
        return segments;
    }

    /**
     * Semantic query within the RAG collection.
     *
     * @param prompt     user query
     * @param maxResults maximum number of results to retrieve
     * @return the document results
     */
    public List<EmbeddingSearchItem> searchRag(String prompt, int maxResults) {
        Embedding queryEmbedding = embeddingModel.embed(prompt).content();
        EmbeddingSearchRequest embeddingSearchRequest = new EmbeddingSearchRequest(queryEmbedding, maxResults, RAG_MIN_SCORE, null);

        EmbeddingSearchResult result = embeddingStore.search(embeddingSearchRequest);
        List<EmbeddingMatch> matches = result.matches();
        return matches.stream()
                .map(embeddingMatch -> {
                    TextSegment textSegment = (TextSegment) embeddingMatch.embedded();
                    return new EmbeddingSearchItem(embeddingMatch.embeddingId(),
                            embeddingMatch.score().floatValue(),
                            textSegment.text());
                })
                .toList();
    }
}
