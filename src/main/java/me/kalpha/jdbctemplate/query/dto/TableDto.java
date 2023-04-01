package me.kalpha.jdbctemplate.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class TableDto {
    private String systemId;
    private String sql;
    private Object[] params;
    private String userId;
    private Table table;
    @Builder.Default
    private Long limit = 10L;
}