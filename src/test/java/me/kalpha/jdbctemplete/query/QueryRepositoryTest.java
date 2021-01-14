package me.kalpha.jdbctemplete.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class QueryRepositoryTest {
    @Autowired
    QueryService queryService;
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
    public void findByQuery() throws Exception {
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

    @Test
    public void queryRecently() throws Exception {
        List list = queryService.queryRecently("batch_job_execution");
        list.stream().forEach(System.out::println);

        assertNotNull(list);
    }

    @Test
    public void validateQuery() throws Exception {
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

        Boolean valid = queryService.validate(queryDto);

        assertTrue(valid);
    }
}