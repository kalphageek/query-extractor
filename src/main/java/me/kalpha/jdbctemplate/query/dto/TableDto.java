package me.kalpha.jdbctemplate.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
public class TableDto {
    @NonNull
    private String systemId;
    @NonNull
    private Table table;
    private Object[] params;
    @Builder.Default
    private Long limit = 10L;
    @NonNull
    private String userId;
}