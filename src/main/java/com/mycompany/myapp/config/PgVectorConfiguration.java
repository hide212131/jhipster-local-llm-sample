package com.mycompany.myapp.config;

import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class PgVectorConfiguration {

    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
        return new PgVectorStore(jdbcTemplate, embeddingClient);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(LiquibaseProperties liquibaseProperties, R2dbcProperties dataSourceProperties) {
        return new JdbcTemplate(createLiquibaseDataSource(liquibaseProperties, dataSourceProperties));
    }

    private static DataSource createLiquibaseDataSource(LiquibaseProperties liquibaseProperties, R2dbcProperties dataSourceProperties) {
        String user = Optional.ofNullable(liquibaseProperties.getUser()).orElse(dataSourceProperties.getUsername());
        String password = Optional.ofNullable(liquibaseProperties.getPassword()).orElse(dataSourceProperties.getPassword());

        return DataSourceBuilder.create().url(liquibaseProperties.getUrl()).username(user).password(password).build();
    }
}
