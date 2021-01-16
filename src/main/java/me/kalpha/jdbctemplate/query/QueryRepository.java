package me.kalpha.jdbctemplate.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QueryRepository {
    public final Integer DEFAULT_LIMITS = 5;
    public final String HUB_BASE_COLUMN = "create_time";
    public final String LAKE_BASE_COLUMN = "lake_load_tm";

    List findByParams(String query, Object[] params);

    Page<List> findByParams(Pageable pageable, String query, Object[] params);

    Boolean validateQueryByParams(String query, Object[] params);

    List findRecently(String tableName);

    List findSample(String tableName);

    Page<List> findSample(Pageable pageable, String tableName);

    Page<List> findRecently(Pageable pageable, String tableName);
}
