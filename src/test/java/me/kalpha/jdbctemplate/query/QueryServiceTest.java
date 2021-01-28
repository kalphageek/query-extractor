package me.kalpha.jdbctemplate.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class QueryServiceTest {
    @Autowired
    QueryService queryService;

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
    public void extract_sample() {
        String tableName = "batch_job_instance";
        long extractCount = queryService.extractSample(tableName);
        System.out.println("extractCount : " + extractCount);

        assertTrue(extractCount > 0);
    }

    @Test
    public void find_sample_pageable() {
        String tableName = "batch_job_instance";

        PageRequest pageable = PageRequest.of(1, 3);
        Page<QueryResult> page = queryService.findSample(pageable, tableName);
        page.stream().forEach(System.out::println);

        assertNotNull(page);
    }

    private QueryDto sampleQueryDto() {
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
    public void validate_query() {
        QueryDto queryDto = sampleQueryDto();

        Boolean valid = queryService.validateQuery(queryDto);

        assertTrue(valid);
    }

    @Test
    public void find_query_pageable() {
        QueryDto queryDto = sampleQueryDto();

        PageRequest pageable = PageRequest.of(0, 5);
        Page<QueryResult> pagedLList = queryService.findByQuery(pageable, queryDto);

        pagedLList.stream().forEach(System.out::println);

        assertNotNull(pagedLList);
    }

    @Test
    public void extract_query() {
        QueryDto queryDto = sampleQueryDto();
        long extractCount = queryService.extractByQuery(queryDto);

        assertTrue(extractCount > 0);
    }

    @Test
    public void find_query() {
        QueryDto queryDto = sampleQueryDto();
        List list = queryService.findByQuery(queryDto);
        printResult(list);

        assertNotNull(list);
    }

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