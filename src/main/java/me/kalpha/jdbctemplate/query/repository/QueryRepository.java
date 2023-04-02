package me.kalpha.jdbctemplate.query.repository;

import me.kalpha.jdbctemplate.query.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface QueryRepository {

    public final Integer DEFAULT_LIMITS = 100;

    public List<Map<String,Object>> findSamples(SamplesDto samplesDto, Integer samplesCount);
//    public List<Object[]> findTable(TableVo tableVo);
    public Object[] findTable(TableVo tableVo) throws SQLException;
    public List<QueryResult> findTable(TableVo tableVo, Long limit);
    public Page<QueryResult> findTable(Pageable pageable, TableVo tableVo);

    public Boolean validateSql(QueryDto queryDto);

    public List<Object[]> findByQuery(QueryDto queryDto);
    public List<QueryResult> findByQuery(QueryDto queryDto, Long limit);
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto);
}
