package com.mycompany.myapp.web.api;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(LlamaCppProperties.CONFIG_PREFIX)
public class LlamaCppProperties {

    public static final String CONFIG_PREFIX = "spring.ai.llama-cpp";

    private String modelHome = "/opt/models";
    private String modelName = "codellama-13b.Q5_K_M.gguf";

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
