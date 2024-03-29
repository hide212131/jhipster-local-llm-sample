package com.mycompany.myapp.web.rest.chat;

import com.mycompany.myapp.service.llm.LlamaCppChatClient;
import com.mycompany.myapp.service.llm.LlamaPrompt;
import com.mycompany.myapp.service.llm.dto.*;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-01-08T22:40:49.631444+09:00[Asia/Tokyo]")
@RestController
@RequestMapping("${openapi.my-llm-app.base-path:/v1}")
public class FluxChatApiController implements FluxChatApi {

    private final LlamaCppChatClient chatClient;

    private final VectorStore vectorStore;

    @Autowired
    public FluxChatApiController(LlamaCppChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
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

            // search related contents from vector store
            // Here, as a provisional behavior, RAG works when the "gpt-4k" model is used.
            if (createChatCompletionRequest.getModel().equals(CreateChatCompletionRequest.ModelEnum._4)) {
                // Find the last UserMessage in the prompt's messages
                var instructions = prompt.getInstructions();
                UserMessage lastUserMessage = null;
                for (int i = instructions.size() - 1; i >= 0; i--) {
                    if (instructions.get(i) instanceof UserMessage) {
                        lastUserMessage = (UserMessage) instructions.get(i);
                        List<Document> results = vectorStore.similaritySearch(
                            SearchRequest.query(lastUserMessage.getContent()).withTopK(5)
                        );
                        String references = results.stream().map(Document::getContent).collect(Collectors.joining("\n"));
                        // replace last UserMessage in prompt's messages with template message with the result and UserMessage
                        var newInstructions = new ArrayList<>(instructions); // Mutable copy of instructions
                        String newMessage =
                            "Please answer the questions in the 'UserMessage'. Find the information you need to answer in the 'References' section. If you do not have the information, please answer with 'I don't know'.\n" +
                            "UserMessage: " +
                            lastUserMessage.getContent() +
                            "\n" +
                            "References: " +
                            references;
                        System.out.println("newMessage: " + newMessage);
                        newInstructions.set(i, new UserMessage(newMessage)); // Replace last UserMessage

                        // Create a new LlamaPrompt with the updated instructions
                        prompt = new LlamaPrompt(newInstructions);
                        break;
                    }
                }
            }

            Flux<ChatResponse> chatResponseFlux = chatClient.stream(prompt);
            var date = System.currentTimeMillis();
            return chatResponseFlux
                .map(chatResponse -> {
                    var responseDelta = new ChatCompletionStreamResponseDelta()
                        .content(chatResponse.getResult().getOutput().getContent())
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
