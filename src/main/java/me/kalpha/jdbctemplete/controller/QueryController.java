package me.kalpha.jdbctemplete.controller;

import me.kalpha.jdbctemplete.domain.QueryDto;
import me.kalpha.jdbctemplete.domain.Row;
import me.kalpha.jdbctemplete.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueryController {

    private final QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/api/query")
    public ResponseEntity query(@RequestBody QueryDto queryDto) {
        List<Row> rowList = queryService.query(queryDto);
        return ResponseEntity.ok(rowList);
    }
}
