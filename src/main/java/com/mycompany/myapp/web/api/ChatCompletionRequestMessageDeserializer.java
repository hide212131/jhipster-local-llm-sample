package com.mycompany.myapp.web.api;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.mycompany.myapp.service.api.dto.ChatCompletionRequestAssistantMessage;
import com.mycompany.myapp.service.api.dto.ChatCompletionRequestMessage;
import com.mycompany.myapp.service.api.dto.ChatCompletionRequestSystemMessage;
import com.mycompany.myapp.service.api.dto.ChatCompletionRequestUserMessage;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class ChatCompletionRequestMessageDeserializer extends JsonDeserializer<ChatCompletionRequestMessage> {

    @Override
    public ChatCompletionRequestMessage deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        String role = node.get("role").asText();

        if ("system".equals(role)) {
            return jp.getCodec().treeToValue(node, ChatCompletionRequestSystemMessage.class);
        } else if ("user".equals(role)) {
            return jp.getCodec().treeToValue(node, ChatCompletionRequestUserMessage.class);
        } else if ("assistant".equals(role)) {
            return jp.getCodec().treeToValue(node, ChatCompletionRequestAssistantMessage.class);
        }

        throw new RuntimeException("Unknown role: " + role);
    }
}
