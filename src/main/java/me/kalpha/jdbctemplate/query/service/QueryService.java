package me.kalpha.jdbctemplate.query.service;

import me.kalpha.jdbctemplate.query.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface QueryService {
    public SampleResponse findSample(SamplesDto samplesDto);

    public List<QueryResult> findTable(TableDto tableDto, Long limit);
    public Page<QueryResult> findTable(Pageable pageable, TableDto tableDto);
    public long extractTable(TableDto tableDto);

    public Boolean validateSql(QueryDto queryDto);

    public List<QueryResult> findByQuery(QueryDto queryDto, Long limit);
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto);
    public long extractByQuery(QueryDto queryDto);
}
