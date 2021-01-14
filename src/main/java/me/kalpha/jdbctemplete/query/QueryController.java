package me.kalpha.jdbctemplete.query;

import me.kalpha.jdbctemplete.common.ErrorsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/query")
public class QueryController {

    private final QueryService queryService;
    private final QueryValidator queryValidator;

    @Autowired
    public QueryController(QueryService queryService, QueryValidator queryValidator) {
        this.queryService = queryService;
        this.queryValidator = queryValidator;
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
    public ResponseEntity validation(@RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validate(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        return ResponseEntity.ok(true);
    }
}
