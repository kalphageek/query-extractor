package me.kalpha.jdbctemplate.query;

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
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class QueryRepositoryOracleImpl  implements QueryRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * 생성자가 1개일때는 @Autowired 생략 가능
     * @param dataSource
     */
    @Autowired
    public QueryRepositoryOracleImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //---------------------------------Non Paging-------------------------------------------
    /**
     * query를 validate한다
     * @param query
     * @param params
     * @return
     */
    @Override
    public Boolean validateQueryByParams(String query, Object[] params) {
        String valdateQuery = "SELECT ( EXISTS (SELECT 1 FROM DUAL)\n" +
                "OR\n" +
                "EXISTS (%s)\n" +
                ") AS result";
        valdateQuery = String.format(valdateQuery, query);
        return jdbcTemplate.queryForObject(valdateQuery, params, Boolean.class);
    }

    /**
     * 1개 Row만 Return 받을때는 rowList.stream().findFirst()또는 findAny() 사용.
     * 두 메소드는 해당 스트림에서 첫 번째 요소를 참조하는 Optional 객체를 반환
     * @param query 사용자 Query
     * @param params Query 파라미터들
     * @return 컬럼을 ArrayList로 가지는 ArrayList ResultSet
     */
    @Override
    public List findByParams(String query, Object[] params) {
        List list = jdbcTemplate.query(query, params, rowMapper());
        return list;
    }

    /**
     * 기준컬럼이 있는 테이블의 경우 기준컬럼으로 order by [기준컬럼] desc 해서 샘플을 가져온다
     * @param tableName
     * @return
     */
    @Override
    public List findRecently(String tableName) {
        String query = String.format("SELECT * FROM %s WHERE ROWNUM < %d ORDER BY %s DESC"
                , tableName, DEFAULT_LIMITS, HUB_BASE_COLUMN);
        List list = jdbcTemplate.query(query, rowMapper());
        return list;
    }

    /**
     * 기준컬럼이 없는 테이블의 경우 order by 없이 random 샘플을 가져온다
     * @param tableName
     * @return
     */
    @Override
    public List findSample(String tableName) {
        String query = String.format("SELECT /*+ INDEX(t DESC) */* FROM %s WHERE ROWNUM < %d"
                , tableName, DEFAULT_LIMITS);
        List list = jdbcTemplate.query(query, rowMapper());
        return list;
    }

    //---------------------------------Paging-----------------------------------------------
    /**
     * @param sql 사용자 Query
     * @param params Query 파라미터들
     * @return 컬럼을 ArrayList로 가지는 ArrayList ResultSet를 Page객체에 담아서 리턴한다
     * Pageable을 RequestParam로 받는 경우 API호출 패턴 : GET /items?after_id=20&limit=20&offset=1&sort_by=desc(last_modified),asc(email)
     */
    @Override
    public Page<List> findByParams(Pageable pageable, String sql, Object[] params) {
        long start = (pageable.getPageSize() - 1) * pageable.getOffset() + 1;
        long end = pageable.getPageSize() * pageable.getOffset();
        String query = String.format("SELECT *\n" +
                        "FROM\n" +
                        "    (\n" +
                        "        SELECT rownum AS rnum, t.*\n" +
                        "        FROM ? as t\n" +
                        "    )\n" +
                        "WHERE rnum BETWEEN ? AND ?"
                , sql, start, end);
        List list = jdbcTemplate.query(query, params, rowMapper());
        return new PageImpl<List>(list, pageable, count(query, params));
    }

    /**
     * 기준컬럼이 있는 테이블의 경우 기준컬럼으로 order by [기준컬럼] desc 해서 샘플을 가져온다
     * @param tableName
     * @return
     */
    @Override
    public Page<List> findRecently(Pageable pageable, String tableName) {
        long start = (pageable.getPageSize() - 1) * pageable.getOffset() + 1;
        long end = pageable.getPageSize() * pageable.getOffset();
//        Sort.Order order = !pageable.getSort().isEmpty() ? pageable.getSort().toList().get(0) : Sort.Order.by(HUB_LOAD_TM);
        Sort.Order order = pageable.getSort().toList().get(0);

        String query = String.format("SELECT *\n" +
                        "FROM\n" +
                        "    (\n" +
                        "        SELECT /*+ INDEX(t DESC) */\n" +
                        "            rownum AS rnum, t.*\n" +
                        "        FROM ? as t\n" +
                        "        ORDER BY ? ?\n" +
                        "    )\n" +
                        "WHERE rnum BETWEEN ? AND ?"
                , tableName, order.getProperty(), order.getDirection(), start, end);
        System.out.println(query);
        List list = jdbcTemplate.query(query, rowMapper());
        return new PageImpl<List>(list, pageable, count(tableName));
    }

    /**
     * 기준컬럼이 없는 테이블의 경우 order by 없이 random 샘플을 가져온다
     * @param tableName
     * @return
     */
    @Override
    public Page<List> findSample(Pageable pageable, String tableName) {
        long start = (pageable.getPageSize() - 1) * pageable.getOffset() + 1;
        long end = pageable.getPageSize() * pageable.getOffset();
        String query = String.format("SELECT *\n" +
                        "FROM\n" +
                        "    (\n" +
                        "        SELECT /*+ INDEX(t DESC) */\n" +
                        "            rownum AS rnum, t.*\n" +
                        "        FROM ? as t\n" +
                        "    )\n" +
                        "WHERE rnum BETWEEN ? AND ?"
                , tableName, start, end);
        log.info("select query : {}", query);
        List list = jdbcTemplate.query(query, rowMapper());
        return new PageImpl<List>(list, pageable, count(tableName));

    }

    private RowMapper<List> rowMapper() {
        return (rs, rowNum) -> {
            List cols = new ArrayList();
            for (int j=1; j<=rs.getMetaData().getColumnCount(); j++) {
                cols.add(rs.getString(j));
            }
            return cols;
        };
    }

    private int count(String query, Object[] params) {
        String countQuery = String.format("SELECT count(*) FROM (%s) as t", query);
        return jdbcTemplate.queryForObject(countQuery, params, Integer.class);
    }

    private int count(String tableName) {
        String countQuery = String.format("SELECT count(*) FROM %s", tableName);
        return jdbcTemplate.queryForObject(countQuery, Integer.class);
    }
}