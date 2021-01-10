package me.kalpha.jdbctemplete.service;

import me.kalpha.jdbctemplete.repository.JdbcTemplateQueryRepository;
import me.kalpha.jdbctemplete.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class QueryConfig {

    private final DataSource dataSource;

    @Autowired
    public QueryConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public QueryService queryService() {
        return new QueryService(queryRepository());
    }

    @Bean
    public QueryRepository queryRepository() {
        return new JdbcTemplateQueryRepository(dataSource);
    }
}