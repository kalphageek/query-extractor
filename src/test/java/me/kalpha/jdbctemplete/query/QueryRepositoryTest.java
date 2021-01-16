package me.kalpha.jdbctemplete.query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class QueryRepositoryTest {
    @Autowired
    private QueryService queryService;
    private String query = "select job_execution_id, version, job_instance_id, create_time, start_time, end_time\n" +
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
    public void findByQuery() {
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
//        list.stream().forEach(System.out::println);

        assertNotNull(list);
    }

    @Test
    public void querySample() {
        List list = queryService.findSample("batch_job_execution");
//        list.stream().forEach(System.out::println);

        assertNotNull(list);
    }

    @Test
    public void queryRecently() {
        List list = queryService.findRecently("batch_job_execution");
//        list.stream().forEach(System.out::println);

        assertNotNull(list);
    }

    @Test
    public void queryRecently_Pageable() {
        String tableName = "batch_job_execution";
        String baseColumn = "create_time";

        PageRequest pageRequest = PageRequest.of(1,5, Sort.Direction.DESC, baseColumn);

        Page<List> pagedList = queryService.findRecently(pageRequest, tableName);
//        pagedList.stream().forEach(System.out::println);

        assertNotNull(pagedList);
    }

    @Test
    public void validateQuery() {
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


    @Test
    public void querySample_Pageable() {
        String tableName = "batch_job_execution";

        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<List> pagedList = queryService.findSample(pageRequest, tableName);
//        pagedList.stream().forEach(System.out::println);

        assertNotNull(pagedList);
    }
}