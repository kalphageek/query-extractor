package me.kalpha.jdbctemplate.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter @Setter
@AllArgsConstructor
@Builder
public class QueryDto {
    private String systemId;
    private String dbType;
    private String sql;
    private Object[] params;
    private String userId;
}
