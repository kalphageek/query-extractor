package me.kalpha.jdbctemplete.repository;

import me.kalpha.jdbctemplete.entity.Row;

import java.util.List;

public interface RowRepository {
    List<Row> findByQuery(String query, String[] conditions);
}
