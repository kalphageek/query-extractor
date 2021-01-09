package me.kalpha.jdbctemplete.controller;

import me.kalpha.jdbctemplete.entity.QueryDto;
import me.kalpha.jdbctemplete.entity.Row;
import me.kalpha.jdbctemplete.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueryController {
    @Autowired
    QueryService queryService;

    @GetMapping("/api/query")
    public ResponseEntity query(@RequestBody QueryDto queryDto) {
        List<Row> rows = queryService.query(queryDto);
        return ResponseEntity.ok(rows);
    }
}
