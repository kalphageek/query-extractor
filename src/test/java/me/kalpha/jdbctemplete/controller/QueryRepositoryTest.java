package me.kalpha.jdbctemplete.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kalpha.jdbctemplete.domain.QueryDto;
import me.kalpha.jdbctemplete.domain.Row;
import me.kalpha.jdbctemplete.service.QueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                "where create_time >= ?\n" +
                "\tand create_time < ?\n" +
                "\tand job_instance_id > ?\n" +
                "\tand status in (?)\n" +
                "\tand exit_message is not null and exit_message <> ''\n" +
                "order by job_instance_id desc, version desc";

        LocalDate createTime1 = LocalDate.parse("2020-10-01");
        LocalDate createTime2 = LocalDate.parse("2020-10-04").plusDays(1);
        Object[] params = {createTime1, createTime2, 20, "FAILED"};

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        sdf.setTimeZone(TimeZone.getDefault());
//        Object[] params = {sdf.parse("20201001")
//                ,sdf.parse("20201030")
//                , 20
//                , "FAILED"};

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
                "where create_time >= ?\n" +
                "\tand create_time < ?\n" +
                "\tand job_instance_id > ?\n" +
                "\tand status in (?)\n" +
                "\tand exit_message is not null and exit_message <> ''\n" +
                "order by job_instance_id desc, version desc";

        LocalDate createTime1 = LocalDate.parse("2020-10-01");
        LocalDate createTime2 = LocalDate.parse("2020-10-04").plusDays(1);

        Object[] params = {createTime1, createTime2, 20, "FAILED"};

        QueryDto queryDto = new QueryDto();
        queryDto.setQuery(query);
        queryDto.setParams(params);
        List<Row> rowList = queryService.query(queryDto);
        rowList.stream().forEach(System.out::println);

        assertNotNull(rowList);
    }
}