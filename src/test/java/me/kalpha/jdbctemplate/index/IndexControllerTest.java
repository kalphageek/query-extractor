package me.kalpha.jdbctemplate.index;

import me.kalpha.jdbctemplate.common.BaseControllerTest;
import org.junit.jupiter.api.Test;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IndexControllerTest extends BaseControllerTest {
    @Test
    public void index() throws Exception {
        mockMvc.perform(get("/data"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.query").exists())
                .andDo(document("index",
                        links(
                                linkWithRel("query").description("The <<resources-query, Query resource>>"),
                                linkWithRel("table").description("The <<resources-table, Table data resource>>")
                        ))
                );
    }
}