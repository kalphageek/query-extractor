package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.BaseControllerTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PagingControllerTest extends BaseControllerTest {
    @Test
    public void findRecently() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(get("/{tableName}/recently/paging", tableName)
                .param("page","0")
                .param("size", "5")
                .param("sort", "job_instance_id,DESC"))
                .andDo(print())
                .andExpect(status().isOk());

    }
}
