package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.ErrorsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping(value = "/data")
public class QueryController {

    private final QueryService queryService;
    private final QueryValidator queryValidator;

    @Autowired
    public QueryController(QueryService queryService, QueryValidator queryValidator) {
        this.queryService = queryService;
        this.queryValidator = queryValidator;
    }

    /**
     * 샘플데이터를 가장 빠른 속도록 조회한다. 조건과 순서를 지정할 수 없다
     * @param tableName Query할 테이블명
     * @return limits한도의 전체 records
     */
    @GetMapping("/{tableName}/samples/all")
    public ResponseEntity findSample(@PathVariable String tableName) {
        List list =  queryService.findSample(tableName);

        // Hateoas (Link 및 Profile)
        CollectionModel<List> outputModel = CollectionModel.of(list);
        outputModel.add(Link.of("/docs/index.html#resources-table-samples-all").withRel("profile"))
                .add(linkTo(this.getClass()).slash(tableName).slash("samples/all").withSelfRel())
                .add(linkTo(this.getClass()).slash(tableName).slash("samples").withRel("table-samples-paging"))
                .add(linkTo(this.getClass()).slash(tableName).slash("samples").withRel("table-samples-extract"));
        return ResponseEntity.ok().body(outputModel);
    }

    /**
     * 샘플데이터를 가장 빠른 속도록 조회한다. 조건과 순서를 지정할 수 없다
     * @param tableName Query할 테이블명
     * @return 추출 레코드 수
     */
    @PostMapping("/{tableName}/samples")
    public ResponseEntity extractSample(@PathVariable String tableName) {
        long extractCount = queryService.extractSample(tableName);
        ExtractResult extractResult = new ExtractResult(extractCount);
        EntityModel<ExtractResult> entityModel = EntityModel.of(extractResult);
        entityModel.add(Link.of("/docs/index.html#resources-table-samples-extract").withRel("profile"))
                .add(linkTo(this.getClass()).slash(tableName).slash("samples").withSelfRel())
                .add(linkTo(this.getClass()).slash(tableName).slash("samples/all").withRel("table-samples-all"))
                .add(linkTo(this.getClass()).slash(tableName).slash("samples").withRel("table-samples-paging"))
        ;
        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * 샘플데이터를 가장 빠른 속도록 조회한다. 조건과 순서를 지정할 수 없다
     * @param pageable 페이지 정보 - size, offset 등
     * @param assembler 페이지 navigation 정보 - fist, prev, page, next, last
     * @param tableName Query할 테이블명
     * @return limits한도의 전체 records
     */
    @GetMapping("/{tableName}/samples")
    public ResponseEntity findSample(Pageable pageable, PagedResourcesAssembler assembler, @PathVariable String tableName) {
        Page<QueryResult> page =  queryService.findSample(pageable, tableName);

        // Hateoas (Link 및 Profile)
        PagedModel pagedModel = assembler.toModel(page, r -> EntityModel.of((QueryResult) r));
        pagedModel.add(Link.of("/docs/index.html#resources-table-samples").withRel("profile"))
                .add(linkTo(this.getClass()).slash(tableName).slash("samples/all").withRel("table-samples-all"))
                .add(linkTo(this.getClass()).slash(tableName).slash("samples").withRel("table-samples-extract"));
        return ResponseEntity.ok().body(pagedModel);
    }

    @GetMapping("/query/validate")
    public ResponseEntity validateQuery(@RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validateQuery(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/query")
    public ResponseEntity findByQuery(Pageable pageable, PagedResourcesAssembler assembler, @RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validateQuery(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        Page<QueryResult> page = queryService.findByQuery(pageable, queryDto);

        // Hateoas (Link 및 Profile)
        PagedModel pagedModel = assembler.toModel(page, r -> PagedModel.of((QueryResult) r));
        pagedModel.add(Link.of("/docs/index.html#resources-query-paging").withRel("profile"))
                .add(linkTo(this.getClass()).slash("/query").withRel("query-extract"));

        return ResponseEntity.ok().body(pagedModel);
    }

    @PostMapping("/query")
    public ResponseEntity extractByQuery(@RequestBody QueryDto queryDto, Errors errors) {
        queryValidator.validateQuery(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }

        long extractCount = queryService.extractByQuery(queryDto);
        ExtractResult extractResult = new ExtractResult(extractCount);

        //Hateoas
        EntityModel<ExtractResult> entityModel = EntityModel.of(extractResult);
        entityModel.add(Link.of("/docs/index.html#resources-query-extract").withRel("profile"))
                .add(linkTo(this.getClass()).slash("/query").withSelfRel())
                .add(linkTo(this.getClass()).slash("/query").withRel("query-paging"));

        return ResponseEntity.ok().body(entityModel);
    }
}