package me.kalpha.jdbctemplate.query;

import org.springframework.hateoas.EntityModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class RowModel extends EntityModel<java.util.List> {
    public static EntityModel<List> modelOf(List list) {
        EntityModel<List> rowModel = EntityModel.of(list);
        rowModel.add(linkTo(QueryController.class).slash(list.stream().findFirst()).withSelfRel());
        return rowModel;
    }
}
