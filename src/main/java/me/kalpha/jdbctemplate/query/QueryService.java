package me.kalpha.jdbctemplate.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryService {
    private final QueryRepository queryRepository;

    @Autowired
    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public List query(QueryDto queryDto) {
        return  queryRepository.findByParams(queryDto.getQuery(), queryDto.getParams());
    }

    public Page<List> query(Pageable pageable, QueryDto queryDto) {
        return  queryRepository.findByParams(pageable, queryDto.getQuery(), queryDto.getParams());
    }

    public Boolean validate(QueryDto queryDto) {
        return  queryRepository.validateQueryByParams(queryDto.getQuery(), queryDto.getParams());
    }

    public List findSample(String tableName) {
        return queryRepository.findSample(tableName);
    }

    public Page<List> findSample(Pageable pageable, String tableName) {
        return queryRepository.findSample(pageable, tableName);
    }

    public List findRecently(String tableName) {
        return queryRepository.findRecently(tableName);
    }

    public Page<List> findRecently(Pageable pageable, String tableName) {
        return queryRepository.findRecently(pageable, tableName);
    }
}