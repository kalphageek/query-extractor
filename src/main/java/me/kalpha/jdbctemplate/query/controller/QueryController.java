package me.kalpha.jdbctemplate.query.controller;

import me.kalpha.jdbctemplate.common.ErrorsModel;
import me.kalpha.jdbctemplate.index.IndexController;
import me.kalpha.jdbctemplate.query.dto.ExtractResult;
import me.kalpha.jdbctemplate.query.dto.QueryDto;
import me.kalpha.jdbctemplate.query.dto.QueryResult;
import me.kalpha.jdbctemplate.query.service.QueryService;
import me.kalpha.jdbctemplate.query.service.QueryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/data/query")
public class QueryController {

    private final QueryService queryService;
    private final QueryValidator queryValidator;

    @Autowired
    public QueryController(QueryService queryService, QueryValidator queryValidator) {
        this.queryService = queryService;
        this.queryValidator = queryValidator;
    }

    @GetMapping("/validate")
    public ResponseEntity validateQuery(@RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validate(queryDto, errors);
        if (errors.hasErrors()) {
            EntityModel errorsModel = ErrorsModel.modelOf(errors);
            errorsModel.add(Link.of("/doc/index.html#overview-errors").withRel("profile"))
                    .add(linkTo(IndexController.class).slash("/data/query").withRel("index"))
                    .add(linkTo(IndexController.class).slash("/data/table").withRel("index"));

            return ResponseEntity.badRequest().body(errorsModel);
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/paging")
    public ResponseEntity findByQuery(@PageableDefault(size = 20, page = 0) Pageable pageable, PagedResourcesAssembler assembler, @RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validate(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        Page<QueryResult> page = queryService.findByQuery(pageable, queryDto);

        // Hateoas (Link 및 Profile)
        PagedModel pagedModel = assembler.toModel(page, r -> PagedModel.of((QueryResult) r));
        pagedModel.add(Link.of("/docs/index.html#resources-query-paging").withRel("profile"))
                .add(linkTo(this.getClass()).withSelfRel())
                .add(linkTo(this.getClass()).withRel("query-extract"))
                .add(linkTo(this.getClass()).withRel("query"));

        return ResponseEntity.ok().body(pagedModel);
    }

    @GetMapping
    public ResponseEntity findByQuery(@RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validate(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        List<QueryResult> results = queryService.findByQuery(queryDto, queryDto.getLimit());

        // Hateoas (Link 및 Profile)
        CollectionModel<QueryResult> outputModel = CollectionModel.of(results);

        outputModel.add(Link.of("/docs/index.html#resources-query").withRel("profile"))
                .add(linkTo(this.getClass()).slash("query").withSelfRel())
                .add(linkTo(this.getClass()).withRel("query-extract"))
                .add(linkTo(this.getClass()).withRel("query-paging"));

        return ResponseEntity.ok().body(outputModel);
    }

    @PostMapping
    public ResponseEntity extractByQuery(@RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validate(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }

        long extractCount = queryService.extractByQuery(queryDto);
        ExtractResult extractResult = new ExtractResult(extractCount);

        //Hateoas
        EntityModel<ExtractResult> entityModel = EntityModel.of(extractResult);
        entityModel.add(Link.of("/docs/index.html#resources-query-extract").withRel("profile"))
                .add(linkTo(this.getClass()).withSelfRel())
                .add(linkTo(this.getClass()).withRel("query-paging"))
                .add(linkTo(this.getClass()).withRel("query"));

        return ResponseEntity.ok().body(entityModel);
    }
}