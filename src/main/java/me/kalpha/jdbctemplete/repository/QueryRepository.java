package me.kalpha.jdbctemplete.repository;

import java.util.List;

public interface QueryRepository {
    List queryByParams(String query, Object[] params);
}
