package me.kalpha.jdbctemplete.controller;

import me.kalpha.jdbctemplete.domain.QueryDto;
import me.kalpha.jdbctemplete.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/query")
public class QueryController {

    private final QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity query(@RequestBody QueryDto queryDto) {
        List list = queryService.query(queryDto);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{tableNm}")
    public ResponseEntity queryRecently(@PathVariable String tableNm) {
        List list = queryService.queryRecently(tableNm);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/validate")
    public ResponseEntity validation(@RequestBody QueryDto queryDto) {
        Boolean isOk;
        try {
            isOk = queryService.validate(queryDto);
        } catch (Exception e) {
            isOk = false;
        }
        return ResponseEntity.ok(isOk);
    }
}
