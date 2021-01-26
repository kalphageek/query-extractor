package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.ErrorsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class JpqlQueryController {

    private final JpqlQueryService jpqlQueryService;
    private final JpqQueryValidator jpqQueryValidator;

    @Autowired
    public JpqlQueryController(JpqlQueryService jpqlQueryService, JpqQueryValidator jpqQueryValidator) {
        this.jpqlQueryService = jpqlQueryService;
        this.jpqQueryValidator = jpqQueryValidator;
    }

    /**
     * 샘플데이터를 가장 빠른 속도록 조회한다. 조건과 순서를 지정할 수 없다
     * @param tableName Query할 테이블명
     * @return limits한도의 전체 records
     */
    @GetMapping("/{tableName}/samples/all")
    public ResponseEntity findSample(@PathVariable String tableName) {
        List list =  jpqlQueryService.findSample(tableName);

        // Hateoas (Link 및 Profile)
        CollectionModel<List> outputModel = CollectionModel.of(list);
        outputModel.add(linkTo(this.getClass()).slash("/" + tableName + "/sample").withSelfRel());
        return ResponseEntity.ok().body(outputModel);
    }

    /**
     * 샘플데이터를 가장 빠른 속도록 조회한다. 조건과 순서를 지정할 수 없다
     * @param tableName Query할 테이블명
     * @return 추출 레코드 수
     */
    @PostMapping("/{tableName}/samples")
    public ResponseEntity extractSample(@PathVariable String tableName) {
        long extractCount = jpqlQueryService.extractSample(tableName);
        String returnValue = String.format("{extractCount:%d}", extractCount);
        return ResponseEntity.ok(returnValue);
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
        Page<QueryResult> page =  jpqlQueryService.findSample(pageable, tableName);

        // Hateoas (Link 및 Profile)
        PagedModel pagedModel = assembler.toModel(page, r -> EntityModel.of((QueryResult) r));
        pagedModel.add(Link.of("/docs/index.html#resources-find-samples").withRel("profile"));
        return ResponseEntity.ok().body(pagedModel);
    }

    @GetMapping("/query/validate")
    public ResponseEntity validateQuery(@RequestBody QueryDto queryDto, Errors errors) {
        jpqQueryValidator.validateQuery(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        return ResponseEntity.ok(true);
    }

    @GetMapping("/query")
    public ResponseEntity findByQuery(Pageable pageable, PagedResourcesAssembler assembler, @RequestBody QueryDto queryDto, Errors errors) {
        jpqQueryValidator.validateQuery(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }
        Page<QueryResult> page = jpqlQueryService.findByQuery(pageable, queryDto);

        // Hateoas (Link 및 Profile)
        PagedModel pagedModel = assembler.toModel(page, r -> PagedModel.of((QueryResult) r));
        pagedModel.add(Link.of("/docs/index.html#resources-query").withRel("profile"));

        return ResponseEntity.ok().body(pagedModel);
    }

    @PostMapping("/query")
    public ResponseEntity extractByQuery(@RequestBody QueryDto queryDto, Errors errors) {
        jpqQueryValidator.validateQuery(queryDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ErrorsModel.modelOf(errors));
        }

        long extractCount = jpqlQueryService.extractByQuery(queryDto);
        String returnValue = String.format("{extractCount:%d}", extractCount);

//        EntityModel<String> entityModel = EntityModel.of(returnValue);
//        entityModel.add(Link.of("/docs/index.html#resources-query").withRel("profile"));

        return ResponseEntity.ok().body(returnValue);
//        return ResponseEntity.ok().body(entityModel);
    }
}