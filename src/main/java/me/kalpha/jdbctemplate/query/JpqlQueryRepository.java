package me.kalpha.jdbctemplate.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JpqlQueryRepository {

    public final Integer DEFAULT_LIMITS = 5;
    public final String HUB_BASE_COLUMN = "job_instance_id";

    public List findSample(String tableName);
    public List extractSample(String tableName);
    public Page<QueryResult> findSample(Pageable pageable, String tableName);

    public Boolean validateQuery(QueryDto queryDto);
    public Page<QueryResult> findQuery(Pageable pageable, QueryDto queryDto);
    public long extractQuery(QueryDto queryDto);
}
