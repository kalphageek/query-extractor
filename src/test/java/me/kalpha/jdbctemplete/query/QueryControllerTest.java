package me.kalpha.jdbctemplete.query;

import me.kalpha.jdbctemplete.common.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QueryControllerTest extends BaseControllerTest {

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

    @Test
    public void findByQuery_Api() throws Exception {
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

        mockMvc.perform(post("/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void queryRecently_Api() throws Exception {
        String tableNm = "batch_job_execution";
        mockMvc.perform(get("/query/{tableNm}", tableNm))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void validateQuery_Api() throws Exception {
        query = "select job_execution_id, version, job_instance_id, create_time, start_time, end_time1\n" +
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

        mockMvc.perform(get("/query/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }
}