package me.kalpha.jdbctemplete.repository;

import me.kalpha.jdbctemplete.domain.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JdbcTemplateQueryRepository implements QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private Integer colCount;
    private ResultSetMetaData rsmd = null;

    /**
     * 생성자가 1개일때는 @Autowired 생략 가능
     * @param dataSource
     */
    public JdbcTemplateQueryRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 1개 Row만 Return 받을때는 rowList.stream().findFirst()또는 findAny() 사용.
     * 두 메소드는 해당 스트림에서 첫 번째 요소를 참조하는 Optional 객체를 반환
     * @param query
     * @param params
     * @return
     */
    @Override
    public List<Row> queryByParams(String query, Object[] params) {
        List<Row> rowList = jdbcTemplate.query(query, params, rowMapper());
        return rowList;
    }

    private RowMapper<Row> rowMapper() {
        return (rs, rowNum) -> {
            if (colCount == null) {
                colCount = rs.getMetaData().getColumnCount();
            }

            List cols = new ArrayList();
            for (int j=1; j<=colCount; j++) {
                cols.add(rs.getString(j));
            }
            Row row = Row.builder()
                    .rownum(rowNum)
                    .cols(cols)
                    .build();
            return row;
        };
    }
}
