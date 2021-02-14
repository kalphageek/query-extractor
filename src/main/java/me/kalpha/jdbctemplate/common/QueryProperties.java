package me.kalpha.jdbctemplate.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Getter @Setter가 모두 필요함
 */
@Configuration
@Getter @Setter
@ConfigurationProperties(prefix = "query")
public class QueryProperties {
    private Integer samplesCount;
}
