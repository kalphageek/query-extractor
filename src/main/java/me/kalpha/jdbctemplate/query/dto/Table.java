package me.kalpha.jdbctemplate.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
@Builder
public class Table {
    @NonNull
    private String select;
    @NonNull
    private String from;
    private String where;
    private String orderBy;
}
