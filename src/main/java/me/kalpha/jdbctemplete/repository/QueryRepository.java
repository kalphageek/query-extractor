package me.kalpha.jdbctemplete.repository;

import me.kalpha.jdbctemplete.entity.Row;

import java.util.List;

public interface QueryRepository {
    List<Row> findByQuery(String query, String[] conditions);
}
