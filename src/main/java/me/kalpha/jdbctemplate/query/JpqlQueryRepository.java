package me.kalpha.jdbctemplate.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JpqlQueryRepository {

    public final Integer DEFAULT_LIMITS = 5;
    public final String HUB_BASE_COLUMN = "job_instance_id";

    List findSample(String tableName);
    long extractSample(String tableName);
    Page<QueryResult> findSample(Pageable pageable, String tableName);
}
