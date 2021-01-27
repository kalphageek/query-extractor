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

public class QueryControllerTest extends BaseControllerTest {
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
        queryDto.setSql(query);
        queryDto.setParams(params);
        return queryDto;
    }

    @Test
    public void find_sample() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(get("/data/{tableName}/samples/all", tableName))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void extract_sample() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(post("/data/{tableName}/samples", tableName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("extractCount").value(QueryRepository.DEFAULT_LIMITS))
        ;
    }

    @Test
    public void find_sample_pageable() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(get("/data/{tableName}/samples", tableName)
                    .param("page","1")
                    .param("size","5"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("정상 : validate Query")
    @Test
    public void query_validate() throws Exception {
        String invalidQuery = query;
        //in절 -->
        QueryDto queryDto = sampleQueryDto(invalidQuery);

        mockMvc.perform(get("/data/query/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("오류 : validate Query")
    @Test
    public void query_validate_error() throws Exception {
        String invalidQuery = query + "aaa";
        //in절 -->
        QueryDto queryDto = sampleQueryDto(invalidQuery);

        mockMvc.perform(get("/data/query/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    public void query_pageable() throws Exception {
        QueryDto queryDto = sampleQueryDto(query);

        mockMvc.perform(get("/data/query")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto))
                    .param("page","0")
                    .param("size","5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryResults[0].record").exists())
        ;
    }

    @Test
    public void query_extract() throws Exception {
        QueryDto queryDto = sampleQueryDto(query);

        mockMvc.perform(post("/data/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("extractCount").exists())
        ;
    }
}
