package com.epam.training.gen.ai.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.EmbeddingItem;
import com.azure.ai.openai.models.Embeddings;
import com.azure.ai.openai.models.EmbeddingsOptions;
import com.epam.training.gen.ai.configuration.ClientOpenAiProperties;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Collections.VectorParams;
import io.qdrant.client.grpc.Points.PointStruct;
import io.qdrant.client.grpc.Points.ScoredPoint;
import io.qdrant.client.grpc.Points.SearchPoints;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static io.qdrant.client.PointIdFactory.id;
import static io.qdrant.client.ValueFactory.value;
import static io.qdrant.client.VectorsFactory.vectors;
import static io.qdrant.client.WithPayloadSelectorFactory.enable;

/**
 * Service class for processing text into embeddings and interacting with Qdrant for vector storage and retrieval.
 * <p>
 * This service converts text into embeddings using Azure OpenAI and saves these vectors in a Qdrant collection.
 * It also provides functionality to search for similar vectors based on input text.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class VectorDbService {
    private final OpenAIAsyncClient openAIAsyncClient;
    private final QdrantClient qdrantClient;
    private final ClientOpenAiProperties clientOpenAiProperties;

    /**
     * Creates a new collection in Qdrant with specified vector parameters.
     *
     * @throws ExecutionException   if the collection creation operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public void createCollection() throws ExecutionException, InterruptedException {
        var result = qdrantClient.createCollectionAsync(
                        clientOpenAiProperties.application().db().collection(),
                        VectorParams.newBuilder()
                                .setDistance(Collections.Distance.Cosine)
                                .setSize(clientOpenAiProperties.application().db().vectorSize())
                                .build())
                .get();
        log.info("Collection was created: [{}]", result.getResult());
    }

    /**
     * Processes the input text into embeddings, transforms them into vector points,
     * and saves them in the Qdrant collection.
     *
     * @param text the text to be processed into embeddings
     * @throws ExecutionException   if the vector saving operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public List<EmbeddingItem> persistEmbeddings(String text) throws ExecutionException, InterruptedException {
        var embeddings = getEmbeddings(text);
        var points = new ArrayList<List<Float>>();
        embeddings.forEach(
                embeddingItem -> {
                    var values = new ArrayList<>(embeddingItem.getEmbedding());
                    points.add(values);
                });

        var pointStructs = new ArrayList<PointStruct>();
        points.forEach(point -> {
            var pointStruct = getPointStruct(text, point);
            pointStructs.add(pointStruct);
        });

        saveVector(pointStructs);

        return embeddings;
    }

    /**
     * Searches the Qdrant collection for vectors similar to the input text.
     * <p>
     * The input text is converted to embeddings, and a search is performed based on the vector similarity.
     *
     * @param text the text to search for similar vectors
     * @return a list of scored points representing similar vectors
     * @throws ExecutionException   if the search operation fails
     * @throws InterruptedException if the thread is interrupted during execution
     */
    public List<ScoredPoint> search(String text) throws ExecutionException, InterruptedException {
        var embeddings = retrieveEmbeddings(text).block();
        var vectors = new ArrayList<Float>();
        embeddings.getData().forEach(embeddingItem ->
                vectors.addAll(embeddingItem.getEmbedding())
        );
        return qdrantClient
                .searchAsync(
                        SearchPoints.newBuilder()
                                .setCollectionName(clientOpenAiProperties.application().db().collection())
                                .addAllVector(vectors)
                                .setWithPayload(enable(true))
                                .setLimit(clientOpenAiProperties.application().db().limit())
                                .build())
                .get();
    }

    /**
     * Retrieves the embeddings for the given text using Azure OpenAI.
     *
     * @param text the text to be embedded
     * @return a list of {@link EmbeddingItem} representing the embeddings
     */
    public List<EmbeddingItem> getEmbeddings(String text) {
        var embeddings = retrieveEmbeddings(text);
        return embeddings.block().getData();
    }


    /**
     * Saves the list of point structures (vectors) to the Qdrant collection.
     *
     * @param pointStructs the list of vectors to be saved
     * @throws InterruptedException if the thread is interrupted during execution
     * @throws ExecutionException   if the saving operation fails
     */
    private void saveVector(ArrayList<PointStruct> pointStructs) throws InterruptedException, ExecutionException {
        var updateResult = qdrantClient
                .upsertAsync(clientOpenAiProperties.application().db().collection(), pointStructs)
                .get();
        log.info(updateResult.getStatus().name());
    }

    /**
     * Constructs a point structure from a list of float values representing a vector.
     *
     * @param text  the text payload
     * @param point the vector values
     * @return a {@link PointStruct} object containing the vector and associated metadata
     */
    private PointStruct getPointStruct(String text, List<Float> point) {
        return PointStruct.newBuilder()
                .setId(id(UUID.randomUUID()))
                .setVectors(vectors(point))
                .putAllPayload(Map.of("text", value(text)))
                .build();
    }

    /**
     * Retrieves the embeddings for the given text asynchronously from Azure OpenAI.
     *
     * @param text the text to be embedded
     * @return a {@link Mono} of {@link Embeddings} representing the embeddings
     */
    private Mono<Embeddings> retrieveEmbeddings(String text) {
        var qembeddingsOptions = new EmbeddingsOptions(List.of(text));
        return openAIAsyncClient.getEmbeddings(clientOpenAiProperties.clientOpenAiEmbeddingModel(), qembeddingsOptions);
    }
}
