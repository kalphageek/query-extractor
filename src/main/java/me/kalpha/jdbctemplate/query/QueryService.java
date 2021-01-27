package me.kalpha.jdbctemplate.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QueryService {
    public List findSample(String tableName);
    public Page<QueryResult> findSample(Pageable pageable, String tableName);
    public long extractSample(String tableName);

    public Boolean validateQuery(QueryDto queryDto);
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto);
    public long extractByQuery(QueryDto queryDto);
}
