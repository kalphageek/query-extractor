package me.kalpha.jdbctemplete.config;

import me.kalpha.jdbctemplete.repository.JdbcTemplteRowRepository;
import me.kalpha.jdbctemplete.repository.RowRepository;
import me.kalpha.jdbctemplete.service.RowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ServiceConfig {
    private final DataSource dataSource;

    @Autowired
    public ServiceConfig(DataSource dataSource){
        this.dataSource = dataSource;
    }

    @Bean
    public RowService rowService() {
        return new RowService(rowRepository());
    }

    @Bean
    private RowRepository rowRepository() {
        return new JdbcTemplteRowRepository(dataSource);
    }

}
