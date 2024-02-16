package com.mycompany.myapp.web.api;

import de.kherud.llama.LlamaModel;
import de.kherud.llama.ModelParameters;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.AbstractEmbeddingClient;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.util.Assert;

public class LlamaCppEmbeddingClient extends AbstractEmbeddingClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String modelHome;

    private String modelName;

    public String getModelHome() {
        return modelHome;
    }

    public void setModelHome(String modelHome) {
        this.modelHome = modelHome;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public List<Double> embed(Document document) {
        LlamaModel.setLogger((level, message) -> System.out.print(message));
        var modelParams = new ModelParameters().setNGpuLayers(1).setEmbedding(true);
        var modelPath = Path.of(modelHome, modelName).toString();
        try (var model = new LlamaModel(modelPath, modelParams)) {
            float[] embedding = model.embed(document.getContent());
            var result = new ArrayList<Double>(embedding.length);
            for (float f : embedding) {
                result.add((double) f);
            }
            return result;
        }
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        Assert.notEmpty(request.getInstructions(), "At least one text is required!");
        if (request.getInstructions().size() != 1) {
            logger.warn("LlamaCpp Embedding does not support batch embedding. Will make multiple API calls to embed(Document)");
        }

        List<List<Double>> embeddingList = new ArrayList<>();
        for (String inputContent : request.getInstructions()) {
            embeddingList.add(embed(new Document(inputContent)));
        }
        var indexCounter = new AtomicInteger(0);

        List<Embedding> embeddings = embeddingList.stream().map(e -> new Embedding(e, indexCounter.getAndIncrement())).toList();
        return new EmbeddingResponse(embeddings);
    }
}
