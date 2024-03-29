package me.kalpha.jdbctemplate.query.controller;

import me.kalpha.jdbctemplate.query.dto.*;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/data/table")
public class TableController {

    private final QueryService queryService;
    private final QueryValidator queryValidator;

    @Autowired
    public TableController(QueryService queryService, QueryValidator queryValidator) {
        this.queryService = queryService;
        this.queryValidator = queryValidator;
    }

    /**
     * 샘플데이터를 가장 빠른 속도록 조회한다. 조건과 순서를 지정할 수 없다
     * @param samplesDto 조히할 테이블명, DBType (ORACLE / OTHERS)
     * @return 샘플데이터
     */
    @GetMapping("/sample")
    public ResponseEntity findSample(@RequestBody SamplesDto samplesDto) {

        SampleResponse response =  queryService.findSample(samplesDto);

        // Hateoas (Link 및 Profile)
        EntityModel<SampleResponse> outputModel = EntityModel.of(response);
        outputModel.add(Link.of("/docs/index.html#resources-table-sample").withRel("profile"))
                .add(linkTo(this.getClass()).slash("table-sample").withSelfRel())
                .add(linkTo(this.getClass()).withRel("table-limit"))
                .add(linkTo(this.getClass()).withRel("table-paging"))
                .add(linkTo(this.getClass()).withRel("table-extract"));
        return ResponseEntity.ok().body(outputModel);
    }

    /**
     * @param tableDto Query할 테이블명, DBType (ORACLE / OTHERS)
     * @return 추출 레코드 수
     */
    @PostMapping("/extract")
    public ResponseEntity extractTable(@RequestBody TableDto tableDto) {
        long extractCount = queryService.extractTable(tableDto);
        ExtractResult extractResult = new ExtractResult(extractCount);
        EntityModel<ExtractResult> entityModel = EntityModel.of(extractResult);
        entityModel.add(Link.of("/docs/index.html#resources-table-extract").withRel("profile"))
                .add(linkTo(this.getClass()).slash("table-extract").withSelfRel())
                .add(linkTo(this.getClass()).withRel("table-paging"))
                .add(linkTo(this.getClass()).withRel("table-limit"))
                .add(linkTo(this.getClass()).withRel("table-sample"))
        ;
        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * 샘플데이터를 가장 빠른 속도록 조회한다. 조건과 순서를 지정할 수 없다
     * @param pageable 페이지 정보 - size, offset 등
     * @param assembler 페이지 navigation 정보 - fist, prev, page, next, last
     * @param tableDto  Query할 테이블명, DBType (ORACLE / OTHERS)
     * @return 샘플데이터
     */
    @GetMapping("/paging")
    public ResponseEntity findTable(@PageableDefault(size = 20, page = 0) Pageable pageable, PagedResourcesAssembler assembler, @RequestBody TableDto tableDto) {
        Page<QueryResult> page =  queryService.findTable(pageable, tableDto);

        // Hateoas (Link 및 Profile)
        PagedModel pagedModel = assembler.toModel(page, r -> EntityModel.of((QueryResult) r));
        pagedModel.add(Link.of("/docs/index.html#resources-table-paging").withRel("profile"))
                .add(linkTo(this.getClass()).slash("table-paging").withSelfRel())
                .add(linkTo(this.getClass()).withRel("table-extract"))
                .add(linkTo(this.getClass()).withRel("table-limit"))
                .add(linkTo(this.getClass()).withRel("table-sample"))
        ;
        return ResponseEntity.ok().body(pagedModel);
    }

    @GetMapping
    public ResponseEntity findTable(@RequestBody TableDto tableDto) {
        List<QueryResult> results = queryService.findTable(tableDto, tableDto.getLimit());
        CollectionModel<QueryResult> outputModel = CollectionModel.of(results);

        outputModel.add(Link.of("/docs/index.html#resources-table-limit").withRel("profile"))
                .add(linkTo(this.getClass()).slash("table-limit").withSelfRel())
                .add(linkTo(this.getClass()).withRel("table-paging"))
                .add(linkTo(this.getClass()).withRel("table-extract"))
                .add(linkTo(this.getClass()).withRel("table-sample"));

        return ResponseEntity.ok().body(outputModel);
    }
}
