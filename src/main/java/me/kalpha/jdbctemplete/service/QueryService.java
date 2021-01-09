package me.kalpha.jdbctemplete.service;

import me.kalpha.jdbctemplete.entity.QueryDto;
import me.kalpha.jdbctemplete.entity.Row;
import me.kalpha.jdbctemplete.repository.QueryRepository;

import java.util.List;

public class QueryService {
    private final QueryRepository queryRepository;

    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public List<Row> query(QueryDto queryDto) {
        return  queryRepository.findByQuery(queryDto.getQuery(), (String[]) queryDto.getConditions().toArray());
    }
}
