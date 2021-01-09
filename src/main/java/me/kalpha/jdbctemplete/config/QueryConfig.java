package me.kalpha.jdbctemplete.config;

import me.kalpha.jdbctemplete.repository.JdbcTemplteQueryRepository;
import me.kalpha.jdbctemplete.repository.QueryRepository;
import me.kalpha.jdbctemplete.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class QueryConfig {
    private final DataSource dataSource;

    @Autowired
    public QueryConfig(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Bean
    public QueryService queryService() {
        return new QueryService(queryRepository());
    }

    @Bean
    private QueryRepository queryRepository() {
        return new JdbcTemplteQueryRepository(dataSource);
    }

}
