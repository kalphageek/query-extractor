package me.kalpha.jdbctemplate.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JpqlQueryServiceTest {
    @Autowired
    JpqlQueryService jpqlQueryService;

    @Test
    public void find_sample() {
        String tableName = "batch_job_instance";
        List list = jpqlQueryService.findSample(tableName);
        list.stream().forEach(System.out::println);

        assertNotNull(list);
    }

    @Test
    public void extract_sample() {
        String tableName = "batch_job_instance";
        long extractCount = jpqlQueryService.extractSample(tableName);
        System.out.println("extractCount : " + extractCount);

        assertTrue(extractCount == JpqlQueryRepository.DEFAULT_LIMITS);
    }

    @Test
    public void find_sample_pageable() {
        String tableName = "batch_job_instance";

        PageRequest pageable = PageRequest.of(1, 3);
        Page<QueryResult> page = jpqlQueryService.findSample(pageable, tableName);
        page.stream().forEach(System.out::println);

        assertNotNull(page);
    }

}