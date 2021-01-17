package me.kalpha.jdbctemplate.query;

import org.springframework.hateoas.EntityModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ResultModel extends EntityModel<java.util.List> {
    public static EntityModel<List> modelOf(List list) {
        EntityModel<List> resultModel = EntityModel.of(list);
        resultModel.add(linkTo(QueryController.class).slash(list.stream().findFirst()).withSelfRel());
        return resultModel;
    }
}
