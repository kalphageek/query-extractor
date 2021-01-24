package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.BaseControllerTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class JpqlQueryControllerTest extends BaseControllerTest {
    @Test
    public void find_sample() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(get("/jpql/{tableName}/samples/all", tableName))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void extract_sample() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(post("/jpql/{tableName}/samples", tableName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("extractCount").value(JpqlQueryRepository.DEFAULT_LIMITS))
        ;
    }

    @Test
    public void find_sample_pageable() throws Exception {
        String tableName = "batch_job_instance";
        mockMvc.perform(get("/jpql/{tableName}/samples", tableName)
                    .param("page","1")
                    .param("size","5"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
