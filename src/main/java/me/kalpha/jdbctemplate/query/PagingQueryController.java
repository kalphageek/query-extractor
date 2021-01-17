package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.ErrorsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class PagingQueryController {

    private final QueryService queryService;
    private final Integer LIMITS = 5;

    @Autowired
    public PagingQueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/{tableName}/recently/paging")
    public ResponseEntity findRecently(@PathVariable String tableName, Pageable pageable, PagedResourcesAssembler assembler) {
        Page<List> list = queryService.findRecently(pageable, tableName);
        return ResponseEntity.ok(list);
    }
}
