package me.kalpha.jdbctemplete.controller;

import me.kalpha.jdbctemplete.entity.Row;
import me.kalpha.jdbctemplete.repository.JdbcTemplteRowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RowControllerTest {
    @Autowired
    JdbcTemplteRowRepository jdbcTemplteRowRepository;

    @Test
    public void query() throws Exception {
        String query = "select job_execution_id, version, job_instance_id, create_time, start_time, end_time\n" +
                "from batch_job_execution\n" +
                "where create_time > '$1'\n" +
                "\tand create_time < '$2'\n" +
                "\tand job_instance_id > $3\n" +
                "\tand status in ('$4','$5')\n" +
                "\tand exit_message is not null and exit_message <> '$6'\n" +
                "order by job_instance_id desc, version desc";
        String[] conditions = {"2020-10-01", "2020-10-30", "20", "FAILED", "WARNNING", ""};

        List<Row> rows = jdbcTemplteRowRepository.findByQuery(query, conditions);
    }
}