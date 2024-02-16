package com.mycompany.myapp.web.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(LlamaCppProperties.CONFIG_PREFIX)
public class LlamaCppProperties {

    public static final String CONFIG_PREFIX = "spring.ai.llama-cpp";

    private String modelHome = System.getProperty("user.dir") + "/models";
    private String modelName = "mistral-7b-instruct-v0.2.Q2_K.gguf";

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
}
