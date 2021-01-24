package me.kalpha.jdbctemplate.query;

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
public class JpqlQueryRepositoryOthersImpl implements JpqlQueryRepository {
    @Autowired
    EntityManager em;

    @Override
    public List findSample(String tableName) {
        String sql = String.format("select t.* from %s as t limit %d"
                , tableName, DEFAULT_LIMITS);
        Query query = em.createNativeQuery(sql);
        return getRecords(query);
    }

    @Override
    public long extractSample(String tableName) {
        String sql = String.format("select t.* from %s as t limit %d"
                , tableName, DEFAULT_LIMITS);
        Query query = em.createNativeQuery(sql);
        return query.getResultList().size();
    }

    @Override
    public Page<QueryResult> findSample(Pageable pageable, String tableName) {
        String sql = String.format("select t.* from %s as t limit %d offset %d"
                , tableName, pageable.getPageSize(), pageable.getOffset());
        Query query = em.createNativeQuery(sql);
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
        String countQuery = String.format("select count(*) from %s", tableName);
        Query query = em.createNativeQuery(countQuery);
        return query.getFirstResult();
    }
}
