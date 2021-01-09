package me.kalpha.jdbctemplete.service;

import me.kalpha.jdbctemplete.entity.QueryDto;
import me.kalpha.jdbctemplete.entity.Row;
import me.kalpha.jdbctemplete.repository.RowRepository;

import java.util.List;

public class RowService {
    private final RowRepository rowRepository;

    public RowService(RowRepository rowRepository) {
        this.rowRepository = rowRepository;
    }

    public List<Row> query(QueryDto queryDto) {
        return  rowRepository.findByQuery(queryDto.getQuery(), (String[]) queryDto.getConditions().toArray());
    }
}
