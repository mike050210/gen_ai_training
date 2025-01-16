package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.EmbeddingSearchItem;
import com.epam.training.gen.ai.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to add and search documents to the RAG dataset
 */
@RestController
@RequestMapping(path = "/api/rag-chat", produces = MimeTypeUtils.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    /**
     * Add a new Document to the RAG Collection.
     *
     * @param document document text
     * @return 200 Status code if successful
     */
    @PostMapping(value = "/documents")
    public ResponseEntity<String> addDocument(@RequestBody String document) {
        ragService.storeDocument(document);
        return ResponseEntity.status(HttpStatus.CREATED).body("Document added");
    }

    /**
     * Search among the documents that are stored in the RAG database.
     *
     * @param prompt search query
     * @param maxResults Maximum results to retrieve
     * @return the RAG Search results
     */
    @PostMapping(value = "/rag-prompt")
    public ResponseEntity<List<EmbeddingSearchItem>> searchRag(
            @RequestBody String prompt,
            @RequestParam(defaultValue = "1") int maxResults) {
        List<EmbeddingSearchItem> response = ragService.searchRag(prompt, maxResults);
        return ResponseEntity.ok(response);
    }
}
