package me.kalpha.jdbctemplete.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kalpha.jdbctemplete.domain.QueryDto;
import me.kalpha.jdbctemplete.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class QueryRepositoryTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    QueryService queryService;

    @Test
    public void findByQuery_Api() throws Exception {
        String query = "select job_execution_id, version, job_instance_id, create_time, start_time, end_time\n" +
                "from batch_job_execution\n" +
                "where 1 = 1\n" +
                "\tand create_time >= to_date(?,'yyyy-MM-dd')\n" +
                "\tand create_time < to_date(?,'yyyy-MM-dd') + 1\n" +
                "\tand job_instance_id > ?\n" +
                "\tand exit_code like ?\n" +
                "\tand exit_message is not null and exit_message <> ''\n" +
                "\tand status in (%s)\n" +
                "order by job_instance_id desc, version desc\n" +
                "limit 10";

        //in절 -->
        List<String> inClouse = new ArrayList<>();
        inClouse.add("FAILED");
        inClouse.add("WARNNING");
        StringBuilder queryBuilder = new StringBuilder();
        for( int i = 0; i< inClouse.size(); i++){
            queryBuilder.append("?");
            if (i !=  inClouse.size() -1) queryBuilder.append(", ");
        }
        query = String.format(query, queryBuilder.toString());
        //in절 <--

        System.out.println(query);
        Object[] params = {"2020-10-01", "2020-10-04", 20, "%"+"FAIL"+"%", inClouse.get(0), inClouse.get(1)};

        QueryDto queryDto = new QueryDto();
        queryDto.setQuery(query);
        queryDto.setParams(params);

        mockMvc.perform(post("/api/query")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void findByQuery() throws Exception {
        String query = "select job_execution_id, version, job_instance_id, create_time, start_time, end_time\n" +
                "from batch_job_execution\n" +
                "where 1 = 1\n" +
                "\tand create_time >= to_date(?,'yyyy-MM-dd')\n" +
                "\tand create_time < to_date(?,'yyyy-MM-dd') + 1\n" +
                "\tand job_instance_id > ?\n" +
                "\tand exit_code like ?\n" +
                "\tand exit_message is not null and exit_message <> ''\n" +
                "\tand status in (%s)\n" +
                "order by job_instance_id desc, version desc\n" +
                "limit 10";

        //in절 -->
        List<String> inClouse = new ArrayList<>();
        inClouse.add("FAILED");
        inClouse.add("WARNNING");
        StringBuilder queryBuilder = new StringBuilder();
        for( int i = 0; i< inClouse.size(); i++){
            queryBuilder.append("?");
            if (i !=  inClouse.size() -1) queryBuilder.append(", ");
        }
        query = String.format(query, queryBuilder.toString());
        //in절 <--

        System.out.println(query);
        Object[] params = {"2020-10-01", "2020-10-04", 20, "%"+"FAIL"+"%", inClouse.get(0), inClouse.get(1)};

        QueryDto queryDto = new QueryDto();
        queryDto.setQuery(query);
        queryDto.setParams(params);

        List list = queryService.query(queryDto);
        list.stream().forEach(System.out::println);

        assertNotNull(list);
    }
}