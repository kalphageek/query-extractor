package me.kalpha.jdbctemplate.query.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
public class QueryResponse {
    private String systemId;
    private String sql;
    private Object[] params;
    private Long limit ;
    private LocalDateTime requiredTime;
    List<Map<String, Object>> records;
}
