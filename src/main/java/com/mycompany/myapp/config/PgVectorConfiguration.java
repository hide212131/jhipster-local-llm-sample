package com.mycompany.myapp.config;

import java.util.Optional;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class PgVectorConfiguration {

    @Bean
    public JdbcTemplate jdbcTemplate(LiquibaseProperties liquibaseProperties, R2dbcProperties dataSourceProperties) {
        var user = Optional.ofNullable(liquibaseProperties.getUser()).orElse(dataSourceProperties.getUsername());
        var password = Optional.ofNullable(liquibaseProperties.getPassword()).orElse(dataSourceProperties.getPassword());
        var dataSource = DataSourceBuilder.create().url(liquibaseProperties.getUrl()).username(user).password(password).build();
        return new JdbcTemplate(dataSource);
    }
}
