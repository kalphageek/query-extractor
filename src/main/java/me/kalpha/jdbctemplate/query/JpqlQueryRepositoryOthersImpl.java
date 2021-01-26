package me.kalpha.jdbctemplate.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
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
    public List<Object[]> extractSample(String tableName) {
        String sql = String.format("select t.* from %s as t limit %d"
                , tableName, DEFAULT_LIMITS);
        Query query = em.createNativeQuery(sql);
        return query.getResultList();
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

    private int count(QueryDto queryDto) {
        String countQuery = String.format("select count(*) from (%s) as t", queryDto.getSql());
        Query query = em.createNativeQuery(countQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return query.getFirstResult();
    }

    @Override
    public Boolean validateQuery(QueryDto queryDto) {
        String valdateQuery = "SELECT ( EXISTS (SELECT 1)\n" +
                "OR\n" +
                "EXISTS (%s)\n" +
                ") AS result";
        valdateQuery = String.format(valdateQuery, queryDto.getSql());
        Query query = em.createNativeQuery(valdateQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return (Boolean) query.getSingleResult();
    }

    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        String pagingQuery = String.format("select * from (%s) as t limit %d offset %d"
                , queryDto.getSql(), pageable.getPageSize(), pageable.getOffset());
        Query query = em.createNativeQuery(pagingQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return getRecords(query, pageable, count(queryDto));
    }

    @Override
    public long extractByQuery(QueryDto queryDto) {
        Query query = em.createNativeQuery(queryDto.getSql());
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        List list = query.getResultList();
        for (int i=0; i<list.size(); i++) {
            Object[] ov = (Object[]) list.get(i);
            StringBuffer sb = new StringBuffer();
            for (int j=0; j< ov.length; j++) {
                if (j == 0) {
                    sb.append(ov[j]);
                } else {
                    sb.append("\t"+ov[j]);
                }
            }
            //TO-DO the code for writing to Isilon
            System.out.println(sb);
            //<--
        }
        return list.size();
    }
}
