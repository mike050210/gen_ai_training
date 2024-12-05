package com.epam.training.gen.ai.controller;

import com.epam.training.gen.ai.model.PromptRequest;
import com.epam.training.gen.ai.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * REST Controller to send image-to-text prompts.
 */
@RestController
@RequestMapping(path = "/api/ai-images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    /**
     * Text-to-image endpoint to generate images.
     *
     * @param request the user's prompt
     * @return the OpenAI response
     */
    @PostMapping(value = "/", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<InputStreamResource> sendTextToImagePrompt(@RequestBody PromptRequest request) {
        validateInput(request.input());

        Flux<byte[]> fluxImage = imageService.generateImageFromText(request.input());

        byte[] imageData = fluxImage
                .collectList()
                .block()
                .stream()
                .reduce(new ByteArrayOutputStream(), (byteArrayOutputStream, bytes) -> {
                    try {
                        byteArrayOutputStream.write(bytes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return byteArrayOutputStream;
                }, (baos1, baos2) -> baos1).toByteArray();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(new ByteArrayInputStream(imageData)));
    }

    private void validateInput(String input) {
        if (StringUtils.isEmpty(input)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'input' parameter is mandatory");
        }
    }
}
