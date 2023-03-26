package me.kalpha.jdbctemplate.index;

import me.kalpha.jdbctemplate.common.BaseControllerTest;
import org.junit.jupiter.api.Test;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IndexControllerTest extends BaseControllerTest {
    @Test
    public void index_query() throws Exception {
        mockMvc.perform(get("/data"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("index",
                        links(
                                linkWithRel("query").description("The <<resources-query, Query>>-Join등 Query를 이용해 필요한 데이터를 얻을 수 있다"),
                                linkWithRel("table").description("The <<resources-table, Table>>-단일 Table에 대한 필드/필터/컬럼순서를 정의해서 데이터를 얻을 수 있다")
                        ))
                );
    }

}