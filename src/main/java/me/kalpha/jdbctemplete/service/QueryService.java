package me.kalpha.jdbctemplete.service;

import me.kalpha.jdbctemplete.domain.QueryDto;
import me.kalpha.jdbctemplete.repository.QueryRepository;

import java.util.List;

public class QueryService {
    private final QueryRepository queryRepository;

    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    //추가
    public List query(QueryDto queryDto) {
        return  queryRepository.queryByParams(queryDto.getQuery(), queryDto.getParams());
    }
}
