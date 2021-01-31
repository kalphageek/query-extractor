package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.domain.QueryDto;
import me.kalpha.jdbctemplate.domain.QueryResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class QueryServiceTest {
    @Autowired
    QueryService queryService;

    @Test
    public void find_samples() {
        QueryDto queryDto = sampleQueryDto();

        List<QueryResult> list = queryService.findSamples(queryDto);
        list.stream().forEach(System.out::println);

        assertNotNull(list);
    }

    @Test
    public void extract_table() {
        QueryDto queryDto = sampleQueryDto();
        long extractCount = queryService.extractTable(queryDto);

        assertTrue(extractCount > 0);
    }

    @Test
    public void find_table_pageable() {
        QueryDto queryDto = sampleQueryDto();

        PageRequest pageable = PageRequest.of(1, 3);
        Page<QueryResult> page = queryService.findTable(pageable, queryDto);
        page.stream().forEach(System.out::println);

        assertNotNull(page);
    }

    @Test
    public void validate_query() {
        QueryDto queryDto = sampleQueryDto();

        Boolean valid = queryService.validateSql(queryDto);

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

    //------------------------------------------------------------------------------------------------
    private QueryDto sampleQueryDto() {
        String select = "job_execution_id,version,job_instance_id,create_time,start_time,end_time,status,exit_code,last_updated";
        String from = "batch_job_execution";
        String where = "create_time >= to_date(?,'yyyy-MM-dd')\n" +
                "\tand create_time < to_date(?,'yyyy-MM-dd') + 1\n" +
                "\tand job_instance_id > ?\n" +
                "\tand exit_code like ?\n" +
                "\tand exit_message is not null and exit_message <> ''\n" +
                "\tand status in (%s)";
        String orderBy = "job_execution_id desc, version desc";
        //in절 -->
        List<String> inClouse = new ArrayList<>();
        inClouse.add("FAILED");
        inClouse.add("WARNNING");
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < inClouse.size(); i++) {
            queryBuilder.append("?");
            if (i != inClouse.size() - 1) queryBuilder.append(", ");
        }
        where = String.format(where, queryBuilder.toString());
        //in절 <--

        Object[] params = {"2020-10-01", "2020-10-04", 20, "%" + "FAIL" + "%", inClouse.get(0), inClouse.get(1)};

        QueryDto.Table table = QueryDto.Table.builder()
                .select(select)
                .from(from)
                .where(where)
                .orderBy(orderBy)
                .build();
        QueryDto queryDto = QueryDto.builder()
                .fileName("file_name")
                .dbType("OTHERS")
                .systemId("100")
                .userId("2043738")
                .params(params)
                .table(table)
                .build();
        queryDto.updateSqlFromTable();

        return queryDto;
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