package me.kalpha.jdbctemplate.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Getter
@ToString
public class QueryResult {
    List record;
}
