package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.domain.QueryDto;
import me.kalpha.jdbctemplate.domain.QueryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QueryService {
    public static final int SAMPLES_COUNT = 100;

    public List<QueryResult> findSamples(QueryDto queryDto);

    public List<Object[]> findTable(QueryDto queryDto);
    public Page<QueryResult> findTable(Pageable pageable, QueryDto queryDto);
    public long extractTable(QueryDto queryDto);

    public Boolean validateSql(QueryDto queryDto);

    public List<Object[]> findByQuery(QueryDto queryDto);
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto);
    public long extractByQuery(QueryDto queryDto);
}
