package me.kalpha.jdbctemplete.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcTemplateQueryRepository implements QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private Integer colCount;
    private ResultSetMetaData rsmd = null;

    /**
     * 생성자가 1개일때는 @Autowired 생략 가능
     * @param dataSource
     */
    @Autowired
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
    public List queryByParams(String query, Object[] params) {
        List list = jdbcTemplate.query(query, params, rowMapper());
        return list;
    }

    private RowMapper<List> rowMapper() {
        return (rs, rowNum) -> {
            if (colCount == null) {
                colCount = rs.getMetaData().getColumnCount();
            }

            List cols = new ArrayList();
            for (int j=1; j<=colCount; j++) {
                cols.add(rs.getString(j));
            }
            return cols;
        };
    }

    @Override
    public Boolean validateQueryByParams(String query, Object[] params) {
        String valdateQuery = "SELECT ( EXISTS (SELECT 1 from dudal)\n" +
                "OR\n" +
                "EXISTS (%s)\n" +
        ") AS result";
        valdateQuery = String.format(valdateQuery, query);
        try {
            return jdbcTemplate.queryForObject(valdateQuery, params, validateMapper());
        } catch (Exception e) {
            return false;
        }
    }

    private RowMapper<Boolean> validateMapper() {
        return (rs, rowNum) -> {
            return rs.getBoolean(1);
        };
    }

    @Override
    public List queryRecently(String tableName) {
        return queryRecently(tableName, DEFAULT_LIMITS);
    }

    @Override
    public List queryRecently(String tableNm, Integer limit) {
        String query = String.format("select * from %s order by %s desc limit %d",tableNm, HUB_BASE_COLUMN, limit);
        System.out.println(query);
        List list = jdbcTemplate.query(query, rowMapper());
        return list;
    }
}