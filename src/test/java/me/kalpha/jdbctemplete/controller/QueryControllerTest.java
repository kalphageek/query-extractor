package me.kalpha.jdbctemplete.controller;

import me.kalpha.jdbctemplete.entity.Row;
import me.kalpha.jdbctemplete.repository.JdbcTemplteQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class QueryControllerTest {
    @Autowired
    JdbcTemplteQueryRepository jdbcTemplteRowRepository;

    @Test
    public void query() throws Exception {
        String query = "select job_execution_id, version, job_instance_id, create_time, start_time, end_time\n" +
                "from batch_job_execution\n" +
                "where create_time > '?'\n" +
                "\tand create_time < '?'\n" +
                "\tand job_instance_id > ?\n" +
                "\tand status in ('?','?')\n" +
                "\tand exit_message is not null and exit_message <> ''\n" +
                "order by job_instance_id desc, version desc";
        String[] conditions = {"2020-10-01", "2020-10-30", "20", "FAILED", "WARNNING"};

        List<Row> rows = jdbcTemplteRowRepository.findByQuery(query, conditions);
    }
}