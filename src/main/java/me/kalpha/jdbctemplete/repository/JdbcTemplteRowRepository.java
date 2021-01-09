package me.kalpha.jdbctemplete.repository;

import me.kalpha.jdbctemplete.entity.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcTemplteRowRepository implements RowRepository {
    private final JdbcTemplate jdbcTemplate;
    private Integer colCount;
    private ResultSetMetaData rsmd = null;

    @Autowired
    public JdbcTemplteRowRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Bean
    @Override
    public List<Row> findByQuery(String query, String[] conditions) {
        return jdbcTemplate.query(query, rowMapper(), conditions);
    }

    private RowMapper<Row> rowMapper() {
        return (rs, i) -> {
            if (colCount == 0) {
                ResultSetMetaData rsmd = rs.getMetaData();
                colCount = rsmd.getColumnCount();
            }

            Map cols = new HashMap();
            for (int j=0; j<colCount; j++) {
                cols.put(rsmd.getColumnName(j), rs.getString(j));
                rs.getString(j);
            }
            Row row = Row.builder()
                    .rownum(i)
                    .cols(cols)
                    .build();

            return row;
        };
    }
}
