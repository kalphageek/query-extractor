package me.kalpha.jdbctemplate.index;


import me.kalpha.jdbctemplate.query.QueryController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class IndexController {
    @GetMapping("/data")
    public RepresentationModel root() {
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(QueryController.class).slash("query").withRel("query"))
             .add(linkTo(QueryController.class).slash("table").withRel("table"));
        return index;
    }
}
