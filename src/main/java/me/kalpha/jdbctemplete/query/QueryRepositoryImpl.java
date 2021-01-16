package me.kalpha.jdbctemplete.query;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class QueryRepositoryImpl implements QueryRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * 생성자가 1개일때는 @Autowired 생략 가능
     * @param dataSource
     */
    @Autowired
    public QueryRepositoryImpl(DataSource dataSource) {
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
    public List findByParams(String query, Object[] params) {
        List list = jdbcTemplate.query(query, params, rowMapper());
        return list;
    }

    private RowMapper<List> rowMapper() {
        List cols = new ArrayList();
        return (rs, rowNum) -> {
            for (int j=1; j<=rs.getMetaData().getColumnCount(); j++) {
                cols.add(rs.getString(j));
            }
            return cols;
        };
    }

    @Override
    public Boolean validateQueryByParams(String query, Object[] params) {
        String valdateQuery = "SELECT ( EXISTS (SELECT 1)\n" +
                "OR\n" +
                "EXISTS (%s)\n" +
                ") AS result";
        valdateQuery = String.format(valdateQuery, query);
        return jdbcTemplate.queryForObject(valdateQuery, params, Boolean.class);
    }

    @Override
    public List findRecently(String tableName) {
        String query = String.format("select * from %s order by %s desc limit %d",tableName, HUB_BASE_COLUMN, DEFAULT_LIMITS);
        List list = jdbcTemplate.query(query, rowMapper());
        return list;
    }

    @Override
    public Page<List> findRecently(Pageable pageable, String tableName) {
//        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : Sort.Order.by(HUB_LOAD_TM);
        Sort.Order order = pageable.getSort().toList().get(0);
        String query = String.format("select * from %s order by %s %s limit %d offset %d"
                , tableName, order.getProperty(), order.getDirection(), pageable.getPageSize(), pageable.getOffset());
        System.out.println(query);
        List list = jdbcTemplate.query(query, rowMapper());
        return new PageImpl<List>(list, pageable, count(tableName));
    }

    @Override
    public List findSample(String tableName) {
        String query = String.format("select * from %s limit %d",tableName, DEFAULT_LIMITS);
        List list = jdbcTemplate.query(query, rowMapper());
        return list;
    }

    @Override
    public Page<List> findSample(Pageable pageable, String tableName) {
        String query = String.format("select * from %s limit %d offset %d"
                , tableName, pageable.getPageSize(), pageable.getOffset());
        log.info("select query : {}", query);
        List list = jdbcTemplate.query(query, rowMapper());
        return new PageImpl<List>(list, pageable, count(tableName));

    }

    private int count(String tableName) {
        String query = String.format("select count(*) from %s", tableName);
        return jdbcTemplate.queryForObject(query, Integer.class);
    }

}