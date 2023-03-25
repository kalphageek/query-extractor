package me.kalpha.jdbctemplate.index;


import me.kalpha.jdbctemplate.query.controller.QueryController;
import me.kalpha.jdbctemplate.query.controller.TableController;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
public class IndexController {
    @GetMapping("/data")
    public RepresentationModel root() {
        RepresentationModel index = new RepresentationModel();
        index.add(linkTo(QueryController.class).withRel("query"))
             .add(linkTo(TableController.class).withRel("table"));
        return index;
    }
}
