package com.mycompany.myapp.web.api;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LlamaCppProperties.class)
public class LlamaCppAutoConfiguration {

    private final LlamaCppProperties llamaCppProperties;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public LlamaCppAutoConfiguration(LlamaCppProperties llamaCppProperties) {
        this.llamaCppProperties = llamaCppProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public LlamaCppChatClient llamaCppChatClient() {
        LlamaCppChatClient llamaCppChatClient = new LlamaCppChatClient();
        llamaCppChatClient.setModelHome(this.llamaCppProperties.getModelHome());
        llamaCppChatClient.setModelName(this.llamaCppProperties.getModelName());
        logger.info("LlamaCppChatClient created: model={}", Path.of(llamaCppChatClient.getModelHome(), llamaCppChatClient.getModelName()));
        return llamaCppChatClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public EmbeddingClient llamaCppEmbeddingClient() {
        var embeddingClient = new LlamaCppEmbeddingClient();
        embeddingClient.setModelHome(this.llamaCppProperties.getModelHome());
        embeddingClient.setModelName(this.llamaCppProperties.getModelName());
        logger.info("LlamaCppEmbeddingClient created: model={}", Path.of(embeddingClient.getModelHome(), embeddingClient.getModelName()));
        return embeddingClient;
    }
}
