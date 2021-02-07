package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.dto.QueryDto;
import me.kalpha.jdbctemplate.dto.QueryResult;
import me.kalpha.jdbctemplate.dto.SamplesDto;
import me.kalpha.jdbctemplate.dto.TableDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QueryRepository {

    public final Integer DEFAULT_LIMITS = 100;

    public List<QueryResult> findSamples(SamplesDto samplesDto);
    public List<Object[]> findTable(TableDto tableDto);
    public Page<QueryResult> findTable(Pageable pageable, TableDto tableDto);

    public Boolean validateSql(QueryDto queryDto);

    public List<Object[]> findByQuery(QueryDto queryDto);
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto);
}
