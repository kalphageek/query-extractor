package me.kalpha.jdbctemplete.repository;

import java.util.List;

public interface QueryRepository {
    public final Integer DEFAULT_LIMITS = 5;
    public final String HUB_BASE_COLUMN = "create_time";
    public final String LAKE_BASE_COLUMN = "lake_load_tm";

    List queryByParams(String query, Object[] params);

    Boolean validateQueryByParams(String query, Object[] params);

    List queryRecently(String tableNm);

    List queryRecently(String tableNm, Integer limits);
}
