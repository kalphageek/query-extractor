package me.kalpha.jdbctemplate.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class QueryCSVResonse {
    private String systemId;
    private String sql;
    private Object[] params;
    private LocalDateTime requiredTime;
    private Map<String, String> columnNames;
    private List<Object[]> records;
}

