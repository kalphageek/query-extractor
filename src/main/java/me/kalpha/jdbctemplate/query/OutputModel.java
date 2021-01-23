package me.kalpha.jdbctemplate.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class OutputModel extends CollectionModel<List> {
    public static CollectionModel<List> modelOf(List list) {
        CollectionModel<List> resultModel = CollectionModel.of(list);
        resultModel.add(linkTo(QueryController.class).slash(list.stream().findFirst()).withSelfRel());
        return resultModel;
    }

    public static CollectionModel<Page> modelOf(Page pagedList) {
        CollectionModel<Page> outputModel = CollectionModel.of(pagedList);
        outputModel.add(linkTo(QueryController.class).slash(pagedList.stream().findFirst()).withSelfRel());
        return outputModel;
    }
}

