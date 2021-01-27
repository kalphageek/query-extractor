package me.kalpha.jdbctemplate.query;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class QueryRepositoryOracleImpl implements QueryRepository {
    @Autowired
    EntityManager em;

    @Override
    public List findSample(String tableName) {
        String sql = String.format("SELECT t.* FROM %s as t WHERE rownum <= %d"
                , tableName, DEFAULT_LIMITS);
        Query query = em.createNativeQuery(sql);
        return getRecords(query);
    }

    @Override
    public Page<QueryResult> findSample(Pageable pageable, String tableName) {
        long start = (pageable.getPageSize() - 1) * pageable.getOffset() + 1;
        long end = pageable.getPageSize() * pageable.getOffset();
        String pagingQuery = String.format(
                        "SELECT t2.*\n" +
                        "  FROM (SELECT rownum AS rnum, t.*\n" +
                        "          FROM %s as t\n" +
                        "       ) as t2\n" +
                        " WHERE rnum BETWEEN %d AND %d"
                , tableName, start, end);
        Query query = em.createNativeQuery(pagingQuery);
        return getRecords(query, pageable, count(tableName));
    }

    private List getRecords(Query query) {
        List<Object[]> queryResults = query.getResultList();

        List records = new ArrayList();
        for (Object[] o : queryResults) {
            records.add(new QueryResult(Arrays.stream(o).collect(Collectors.toList())));
        }
        return records;
    }

    private Page<QueryResult> getRecords(Query query, Pageable pageable, long count) {
        return new PageImpl<QueryResult>(getRecords(query), pageable, count);
    }

    private long count(String tableName) {
        String countQuery = String.format("SELECT count(*) FROM %s", tableName);
        Query query = em.createNativeQuery(countQuery);
        return query.getFirstResult();
    }

    private int count(QueryDto queryDto) {
        String countQuery = String.format("SELECT count(*) FROM (%s) as t", queryDto.getSql());
        Query query = em.createNativeQuery(countQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return query.getFirstResult();
    }

    /**
     * 1개 Row만 Return 받을때는 rowList.stream().findFirst()또는 findAny() 사용.
     * 두 메소드는 해당 스트림에서 첫 번째 요소를 참조하는 Optional 객체를 반환
     * @param queryDto
     * @return
     */
    @Override
    public Boolean validateQuery(QueryDto queryDto) {
        String valdateQuery = String.format(
                "SELECT ( EXISTS (SELECT 1 FROM DUAL)\n" +
                "         OR\n" +
                "         EXISTS (%s)\n" +
                "       ) AS result\n" +
                "  FROM dual", queryDto.getSql());
        Query query = em.createNativeQuery(valdateQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return (Boolean) query.getSingleResult();
    }

    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        long start = (pageable.getPageSize() - 1) * pageable.getOffset() + 1;
        long end = pageable.getPageSize() * pageable.getOffset();
        String pagingQuery = String.format("SELECT *\n" +
                        "  FROM (SELECT rownum AS rnum, t.*\n" +
                        "          FROM ? as t\n" +
                        "       )\n" +
                        " WHERE rnum BETWEEN ? AND ?"
                , queryDto.getSql(), start, end);
        Query query = em.createNativeQuery(pagingQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return getRecords(query, pageable, count(queryDto));
    }

    @Override
    public List findByQuery(QueryDto queryDto) {
        Query query = em.createNativeQuery(queryDto.getSql());
        for (int i = 0; i < queryDto.getParams().length; i++) {
            query.setParameter(i + 1, queryDto.getParams()[i]);
        }
        return query.getResultList();
    }
}