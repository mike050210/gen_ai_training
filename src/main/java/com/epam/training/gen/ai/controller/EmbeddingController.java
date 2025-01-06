package com.epam.training.gen.ai.controller;

import com.azure.ai.openai.models.EmbeddingItem;
import com.epam.training.gen.ai.model.EmbeddingRequest;
import com.epam.training.gen.ai.model.EmbeddingSearchItem;
import com.epam.training.gen.ai.service.VectorDbService;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * API controller to handle embedding petitions with Qdrant Database.
 */
@RestController
@RequestMapping(path = "/api/embeddings", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EmbeddingController {

    public final VectorDbService vectorDbService;

    /**
     * Endpoint to create a new Qdrant collection.
     *
     * @return 200 Status code if successful
     * @throws ExecutionException   in case the execution fails
     * @throws InterruptedException in case an interruption happens
     */
    @PostMapping(value = "/collection")
    public ResponseEntity<String> createNewCollection() throws ExecutionException, InterruptedException {
        vectorDbService.createCollection();
        return ResponseEntity.status(HttpStatus.CREATED).body("Collection created");
    }

    /**
     * Endpoint to visualize the generation of embeddings given a text input.
     *
     * @param request the text input request
     * @return a list of Embeddings (vectors) given the input
     * @throws ExecutionException   in case the execution fails
     * @throws InterruptedException in case an interruption happens
     */
    @PostMapping(value = "/preview")
    public ResponseEntity<List<EmbeddingItem>> getEmbeddings(@RequestBody EmbeddingRequest request)
            throws ExecutionException, InterruptedException {
        var embeddings = vectorDbService.getEmbeddings(request.text());
        return ResponseEntity.ok(embeddings);
    }

    /**
     * Endpoint to generate and persist embeddings from an input text. The embeddings would be stored
     * in a Qdrant DB.
     *
     * @param request the text input request
     * @return a list of Embeddings (vectors) given the input
     * @throws ExecutionException   in case the execution fails
     * @throws InterruptedException in case an interruption happens
     */
    @PostMapping()
    public ResponseEntity<List<EmbeddingItem>> saveEmbeddings(@RequestBody EmbeddingRequest request)
            throws ExecutionException, InterruptedException {
        var embeddings = vectorDbService.persistEmbeddings(request.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(embeddings);
    }

    /**
     * Endpoint to run a semantic search given an input text.
     *
     * @param text input to search in the Qdrant DB.
     * @return a list of the approximate closest points
     * @throws ExecutionException   in case the execution fails
     * @throws InterruptedException in case an interruption happens
     */
    @GetMapping()
    public ResponseEntity<List<EmbeddingSearchItem>> search(@RequestParam String text)
            throws ExecutionException, InterruptedException {
        var scoredPoints = vectorDbService.search(text);
        var response = mapScoredPointsToSearchItem(scoredPoints);

        return ResponseEntity.ok(response);
    }

    private List<EmbeddingSearchItem> mapScoredPointsToSearchItem(List<Points.ScoredPoint> scoredPoints) {
        List<EmbeddingSearchItem> embeddingSearchItemList = new ArrayList<>();
        scoredPoints.forEach(scoredPoint -> {
            var id = scoredPoint.getId().getUuid();
            var score = scoredPoint.getScore();
            var payload = scoredPoint.getPayloadOrDefault("text", null);
            var payloadString = payload == null ? "" : payload.getStringValue();

            EmbeddingSearchItem embeddingSearchItem = new EmbeddingSearchItem(id, score, payloadString);
            embeddingSearchItemList.add(embeddingSearchItem);
        });

        return embeddingSearchItemList;
    }
}
