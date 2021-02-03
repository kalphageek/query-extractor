package me.kalpha.jdbctemplate.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "query")
public class QueryProperties {
    private Integer samplesCount;
}
