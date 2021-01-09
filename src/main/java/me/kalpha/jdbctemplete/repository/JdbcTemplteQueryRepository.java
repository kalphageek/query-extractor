package me.kalpha.jdbctemplete.repository;

import me.kalpha.jdbctemplete.entity.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplteQueryRepository implements QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private Integer colCount;
    private ResultSetMetaData rsmd = null;

    @Autowired
    public JdbcTemplteQueryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Row> findByQuery(String query, String[] conditions) {
        return jdbcTemplate.query(query, rowMapper(), conditions);
    }

    private RowMapper<Row> rowMapper() {
        return (rs, i) -> {
            if (colCount == 0) {
                colCount = rs.getMetaData().getColumnCount();
            }

            List cols = new ArrayList();
            for (int j=0; j<colCount; j++) {
                cols.add(rs.getString(j));
            }
            Row row = Row.builder()
                    .rownum(i)
                    .cols(cols)
                    .build();

            return row;
        };
    }
}
