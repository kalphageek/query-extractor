package me.kalpha.jdbctemplete.repository;

import me.kalpha.jdbctemplete.domain.Row;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface QueryRepository {
    List<Row> queryByParams(String query, Object[] params);
}
