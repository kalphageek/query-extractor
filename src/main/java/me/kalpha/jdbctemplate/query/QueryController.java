package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.ErrorsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping
public class QueryController {

    private final QueryService queryService;
    private final QueryValidator queryValidator;
    private final Integer LIMITS = 5;

    @Autowired
    public QueryController(QueryService queryService, QueryValidator queryValidator) {
        this.queryService = queryService;
        this.queryValidator = queryValidator;
    }

    //---------------------------------Non Paging-------------------------------------------
    @GetMapping("/query/validate")
    public ResponseEntity validate(@RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validate(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/query")
    public ResponseEntity query(@RequestBody QueryDto queryDto) {
        List list = queryService.query(queryDto);
        CollectionModel<List> outputModel = CollectionModel.of(list);
        outputModel.add(linkTo(this.getClass()).slash("/query").withSelfRel());
        return ResponseEntity.ok(outputModel);
    }

    @GetMapping("/{tableName}/recently")
    public ResponseEntity findRecently(@PathVariable String tableName) {
        List list = queryService.findRecently(tableName);
        CollectionModel<List> outputModel = CollectionModel.of(list);
        outputModel.add(linkTo(this.getClass()).slash("/" + tableName + "/recently").withSelfRel());
        return ResponseEntity.ok(outputModel);
    }

    @GetMapping("/{tableName}/sample")
    public ResponseEntity findSample(@PathVariable String tableName) {
        List list =  queryService.findSample(tableName);
        CollectionModel<List> outputModel = CollectionModel.of(list);
        outputModel.add(linkTo(this.getClass()).slash("/" + tableName + "/sample").withSelfRel());
        return ResponseEntity.ok(outputModel);
    }

    //---------------------------------Paging-----------------------------------------------
    @GetMapping("/query/{page}")
    public ResponseEntity query(@RequestBody QueryDto queryDto, @PathVariable Integer page) {
        PageRequest pageable = PageRequest.of(page, LIMITS);
        Page pagedList = queryService.query(pageable, queryDto);
        CollectionModel<Page> resultModel = OutputModel.modelOf(pagedList);
        return ResponseEntity.ok(resultModel);
    }

    @GetMapping("/{tableName}/recently/{page}")
    public ResponseEntity findRecently(@PathVariable String tableName, @PathVariable Integer page) {
        PageRequest pageable = PageRequest.of(page, LIMITS, Sort.Direction.DESC, "job_instance_id");
        Page pagedList = queryService.findRecently(pageable, tableName);
        return ResponseEntity.ok(pagedList);
    }

    @GetMapping("/{tableName}/sample/{page}")
    public ResponseEntity findSample(@PathVariable String tableName, @PathVariable Integer page) {
        PageRequest pageable = PageRequest.of(page, LIMITS);
        Page pagedList =  queryService.findSample(pageable, tableName);
        return ResponseEntity.ok(pagedList);
    }
}
