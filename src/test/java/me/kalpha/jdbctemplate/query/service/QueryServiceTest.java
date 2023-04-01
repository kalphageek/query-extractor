package me.kalpha.jdbctemplate.query.service;

import me.kalpha.jdbctemplate.query.GenerateTestData;
import me.kalpha.jdbctemplate.query.dto.*;
import me.kalpha.jdbctemplate.query.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QueryServiceTest {
    @Autowired
    QueryService queryService;

    @Test
    public void find_samples() {
        SamplesDto samplesDto = GenerateTestData.generateSamplesDto();

        SampleResponse response = queryService.findSample(samplesDto);
        response.getRecords().stream().forEach(System.out::println);
    }

    @Test
    public void extract_table() {
        TableDto tableDto = GenerateTestData.generateTableDto();

        long extractCount = queryService.extractTable(tableDto);

        assertTrue(extractCount > 0);
    }

    @Test
    public void find_table() {
        TableDto tableDto = GenerateTestData.generateTableDto();

        List<QueryResult> results = queryService.findTable(tableDto, 20L);
        results.stream().forEach(System.out::println);

        assertTrue(results.size() == tableDto.getLimit());
    }
    @Test
    public void find_table_pageable() {
        TableDto tableDto = GenerateTestData.generateTableDto();

        PageRequest pageable = PageRequest.of(1, 3);
        Page<QueryResult> page = queryService.findTable(pageable, tableDto);
        page.stream().forEach(System.out::println);

        assertNotNull(page);
    }

    @Test
    public void validate_query() {
        QueryDto queryDto = GenerateTestData.generateQueryDto();

        Boolean valid = queryService.validateSql(queryDto);

        assertTrue(valid);
    }

    @Test
    public void find_query_pageable() {
        QueryDto queryDto = GenerateTestData.generateQueryDto();

        PageRequest pageable = PageRequest.of(0, 5);
        Page<QueryResult> pagedLList = queryService.findByQuery(pageable, queryDto);

        pagedLList.stream().forEach(System.out::println);

        assertNotNull(pagedLList);
    }

    @Test
    public void extract_query() {
        QueryDto queryDto = GenerateTestData.generateQueryDto();

        long extractCount = queryService.extractByQuery(queryDto);

        assertTrue(extractCount > 0);
    }



    //------------------------------------------------------------------------------------------------

    private void printResult(List list) {
        for (int i=0; i<list.size(); i++) {
            Object[] ov = (Object[]) list.get(i);
            StringBuffer sb = new StringBuffer();
            for (int j=0; j< ov.length; j++) {
                if (j == 0) {
                    sb.append(ov[j]);
                } else {
                    sb.append("\t"+ov[j]);
                }
            }
            System.out.println(sb);
        }
    }
}