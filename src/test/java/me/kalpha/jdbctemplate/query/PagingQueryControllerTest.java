package me.kalpha.jdbctemplate.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PagingQueryControllerTest extends BaseControllerTest {

    @Test
    public void findRecently() throws Exception {
        String tableName = "batch_job_execution";
        mockMvc.perform(get("/{tableName}/recently/paging", tableName)
                    .param("page","0")
                    .param("size", "5")
                    .param("sort", "create_time,DESC"))
                .andDo(print())
                .andExpect(status().isOk());

    }
}