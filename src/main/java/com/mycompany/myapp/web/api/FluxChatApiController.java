package com.mycompany.myapp.web.api;

import com.mycompany.myapp.service.api.dto.*;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.prompt.messages.AssistantMessage;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.ai.prompt.messages.SystemMessage;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-01-08T22:40:49.631444+09:00[Asia/Tokyo]")
@RestController
@RequestMapping("${openapi.my-llm-app.base-path:/v1}")
public class FluxChatApiController implements FluxChatApi {

    private final LlamaCppChatClient chatClient;

    @Autowired
    public FluxChatApiController(LlamaCppChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Flux<CreateChatCompletionStreamResponse> createChatCompletion(
        @Parameter(name = "CreateChatCompletionRequest", description = "", required = true) @Valid @RequestBody Mono<
            CreateChatCompletionRequest
        > createChatCompletionRequestMono,
        @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        return createChatCompletionRequestMono.flatMapMany(createChatCompletionRequest -> {
            var messages = createChatCompletionRequest.getMessages();
            var prompt = new LlamaPrompt(
                messages
                    .stream()
                    .map(message ->
                        (Message) switch (message) {
                            case ChatCompletionRequestSystemMessage systemMessage -> new SystemMessage(systemMessage.getContent());
                            case ChatCompletionRequestUserMessage userMessage -> new UserMessage(userMessage.getContent());
                            case ChatCompletionRequestAssistantMessage assistantMessage -> new AssistantMessage(
                                assistantMessage.getContent()
                            );
                            case null, default -> throw new RuntimeException("Unknown message type");
                        }
                    )
                    .toList()
            );

            Flux<ChatResponse> chatResponseFlux = chatClient.generateStream(prompt);
            var date = System.currentTimeMillis();
            return chatResponseFlux
                .map(chatResponse -> {
                    var responseDelta = new ChatCompletionStreamResponseDelta()
                        .content(chatResponse.getGeneration().getContent())
                        .role(ChatCompletionStreamResponseDelta.RoleEnum.ASSISTANT);
                    var choices = new CreateChatCompletionStreamResponseChoicesInner().index(0L).finishReason(null).delta(responseDelta);
                    return new CreateChatCompletionStreamResponse(
                        "chatcmpl-123",
                        List.of(choices),
                        date,
                        "gpt-3.5-turbo",
                        CreateChatCompletionStreamResponse.ObjectEnum.CHAT_COMPLETION_CHUNK
                    );
                })
                .concatWithValues(
                    new CreateChatCompletionStreamResponse(
                        "chatcmpl-123",
                        List.of(
                            new CreateChatCompletionStreamResponseChoicesInner()
                                .index(0L)
                                .finishReason(CreateChatCompletionStreamResponseChoicesInner.FinishReasonEnum.STOP)
                                .delta(new ChatCompletionStreamResponseDelta())
                        ),
                        date,
                        "gpt-3.5-turbo",
                        CreateChatCompletionStreamResponse.ObjectEnum.CHAT_COMPLETION_CHUNK
                    )
                );
        });
    }
}
