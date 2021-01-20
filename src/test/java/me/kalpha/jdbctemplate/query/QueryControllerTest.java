package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QueryControllerTest extends BaseControllerTest {

    private String query = "select job_execution_id,version,job_instance_id,create_time,start_time,end_time,status,exit_code,last_updated\n" +
            "from batch_job_execution\n" +
            "where 1 = 1\n" +
            "\tand create_time >= to_date(?,'yyyy-MM-dd')\n" +
            "\tand create_time < to_date(?,'yyyy-MM-dd') + 1\n" +
            "\tand job_instance_id > ?\n" +
            "\tand exit_code like ?\n" +
            "\tand exit_message is not null and exit_message <> ''\n" +
            "\tand status in (%s)\n" +
            "order by job_execution_id desc, version desc";

    @Test
    public void query() throws Exception {

        QueryDto queryDto = sampleQueryDto(query);

        mockMvc.perform(get("/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void query_Pageable() throws Exception {

        QueryDto queryDto = sampleQueryDto(query);

        mockMvc.perform(get("/query/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    private QueryDto sampleQueryDto(String query) {
        //in절 -->
        List<String> inClouse = new ArrayList<>();
        inClouse.add("FAILED");
        inClouse.add("WARNNING");
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < inClouse.size(); i++) {
            queryBuilder.append("?");
            if (i != inClouse.size() - 1) queryBuilder.append(", ");
        }
        query = String.format(query, queryBuilder.toString());
        //in절 <--

        System.out.println(query);
        Object[] params = {"2020-10-01", "2020-10-04", 20, "%" + "FAIL" + "%", inClouse.get(0), inClouse.get(1)};

        QueryDto queryDto = new QueryDto();
        queryDto.setDbType("OTHERS");
        queryDto.setQuery(query);
        queryDto.setParams(params);
        return queryDto;
    }

    @Test
    public void table_Recently() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(get("/{tableName}/recently", tableName))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void table_Recently_Pageable() throws Exception {
        String tableName = "batch_job_instance";
        String page = "0";
        mockMvc.perform(get("/{tableName}/recently/{page}", tableName, page))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void table_Sample() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(get("/{tableName}/sample", tableName))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void table_Sample_Pageable() throws Exception {
        String tableName = "batch_job_instance";
        String  page = "0";
        mockMvc.perform(get("/{tableName}/sample/{page}", tableName, page))
                .andDo(print())
                .andExpect(status().isOk())
        ;
        //@RequestParam으로 받는 경우
//        mockMvc.perform(get("/{tableName}/sample", tableName)
//                    .param("page", page))
//                .andDo(print())
//                .andExpect(status().isOk())
//        ;
    }

    @DisplayName("Query Validation 체크")
    @Test
    public void query_Validate() throws Exception {
        String invalidQuery = query + "aaa";
        //in절 -->
        QueryDto queryDto = sampleQueryDto(invalidQuery);

        mockMvc.perform(get("/query/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
}