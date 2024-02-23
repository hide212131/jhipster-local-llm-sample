package com.mycompany.myapp.web.api;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
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
        // Because the LlamaCpp embedding is 4096-dimensional, we need to reduce the dimension to 2000
        // using a random projection matrix.
        RealMatrix randomMatrix = getRandomMatrix();
        var embeddingClient = new LlamaCppEmbeddingClient() {
            @Override
            public List<Double> embed(Document document) {
                List<Double> result = super.embed(document);
                return reduceDimension(randomMatrix, result);
            }
        };
        embeddingClient.setModelHome(this.llamaCppProperties.getModelHome());
        embeddingClient.setModelName(this.llamaCppProperties.getModelName());
        logger.info("LlamaCppEmbeddingClient created: model={}", Path.of(embeddingClient.getModelHome(), embeddingClient.getModelName()));
        return embeddingClient;
    }

    private static final int LLAMA_CPP_EMBEDDING_DIMENSION = 4096;
    private static final int PGVECTOR_DEFAULT_DIMENSION = 1536;

    @NotNull
    private static RealMatrix getRandomMatrix() {
        RealMatrix randomMatrix = new Array2DRowRealMatrix(PGVECTOR_DEFAULT_DIMENSION, LLAMA_CPP_EMBEDDING_DIMENSION);
        RandomDataGenerator randomData = new RandomDataGenerator();
        for (int i = 0; i < LLAMA_CPP_EMBEDDING_DIMENSION; i++) {
            for (int j = 0; j < PGVECTOR_DEFAULT_DIMENSION; j++) {
                randomMatrix.setEntry(j, i, randomData.nextGaussian(0, 1));
            }
        }
        return randomMatrix;
    }

    private List<Double> reduceDimension(RealMatrix randomMatrix, List<Double> result) {
        // Ensure the size of the result list is equal to LLAMA_CPP_EMBEDDING_DIMENSION
        if (result.size() != LLAMA_CPP_EMBEDDING_DIMENSION) {
            throw new IllegalArgumentException(
                "Invalid size of result list: expected size " + LLAMA_CPP_EMBEDDING_DIMENSION + ", actual size " + result.size()
            );
        }

        RealMatrix resultMatrix = new Array2DRowRealMatrix(1, result.size());
        for (int i = 0; i < result.size(); i++) {
            resultMatrix.setEntry(0, i, result.get(i));
        }
        RealMatrix reducedMatrix = randomMatrix.multiply(resultMatrix.transpose()).transpose();
        double[] row = reducedMatrix.getRow(0);
        return Arrays.stream(row).boxed().toList();
    }
}
