package com.epam.training.gen.ai.service;

import com.epam.training.gen.ai.model.ImageMessageRequest;
import com.epam.training.gen.ai.model.ImagePromptRequest;
import com.epam.training.gen.ai.model.dial.ChatCompletionResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service that handles the Text-to-image prompts.
 */
@Service
public class ImageService {

    private final WebClient webClient;
    private final WebClient.RequestBodySpec requestBodySpec;

    public ImageService(
            @Qualifier("dialWebClient") final WebClient webClient,
            @Qualifier("dialBodySpec") final WebClient.RequestBodySpec requestBodySpec) {
        this.webClient = webClient;
        this.requestBodySpec = requestBodySpec;
    }

    private static final String PATH_FILES_VERSION = "/v1/";


    /**
     * Generates an image from a user prompt.
     *
     * @param prompt user input
     * @return the Image in bytes
     */
    public Flux<byte[]> generateImageFromText(String prompt) {
        ImagePromptRequest imagePromptRequest = getPromptRequest(prompt);

        return requestBodySpec
                .bodyValue(imagePromptRequest)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .map(this::getImageUrl)
                .flatMapMany(image -> image.map(imageUrl -> getImageBytes(imageUrl)).orElse(null));
    }

    private ImagePromptRequest getPromptRequest(String prompt) {
        List<ImageMessageRequest> messages = new ArrayList<>();
        messages.add(new ImageMessageRequest("user", "url," + prompt));
        return new ImagePromptRequest(messages);
    }

    private Optional<String> getImageUrl(ChatCompletionResponse chatCompletionResponse) {
        return chatCompletionResponse.choices()
                .stream()
                .findFirst()
                .map(choice -> choice.message().customContent().attachments())
                .flatMap(attachments -> attachments
                        .stream()
                        .filter(attachment -> StringUtils.isNotEmpty(attachment.url()))
                        .findFirst().map(attachment -> attachment.url()));
    }


    private Flux<byte[]> getImageBytes(String imageUrl) {
        return this.webClient
                .get()
                .uri(PATH_FILES_VERSION + imageUrl)
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .bodyToFlux(byte[].class);
    }
}
