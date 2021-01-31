package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.domain.QueryDto;
import me.kalpha.jdbctemplate.domain.QueryResult;
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
public class QueryRepositoryOthersImpl implements QueryRepository {
    @Autowired
    EntityManager em;

    @Override
    public Boolean validateSql(QueryDto queryDto) {
        String valdateSql =
                "select (exists (select 1)\n" +
                        "        or\n" +
                        "        exists (%s)\n" +
                        ") as result";
        valdateSql = String.format(valdateSql, queryDto.getSql());
        Query query = em.createNativeQuery(valdateSql);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return (Boolean) query.getSingleResult();
    }

    @Override
    public List<QueryResult> findSamples(QueryDto queryDto) {
        String samplesSql = String.format("select * from %s limit %d", queryDto.getTable().getFrom(), QueryService.SAMPLES_COUNT);
        Query query = em.createNativeQuery(samplesSql);
        return getRecords(query);
    }

    @Override
    public List<Object[]> findTable(QueryDto queryDto) {
        Query query = em.createNativeQuery(queryDto.getSql());
        for (int i = 0; i < queryDto.getParams().length; i++) {
            query.setParameter(i + 1, queryDto.getParams()[i]);
        }
        return query.getResultList();
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, QueryDto queryDto) {
        String sql = String.format("%s limit %d offset %d"
                , queryDto.getSql(), pageable.getPageSize(), pageable.getOffset());
        Query query = em.createNativeQuery(sql);
        for (int i = 0; i < queryDto.getParams().length; i++) {
            query.setParameter(i + 1, queryDto.getParams()[i]);
        }
        return getRecords(query, pageable, count(queryDto));
    }

    @Override
    public List<Object[]> findByQuery(QueryDto queryDto) {
        Query query = em.createNativeQuery(queryDto.getSql());
        for (int i = 0; i < queryDto.getParams().length; i++) {
            query.setParameter(i + 1, queryDto.getParams()[i]);
        }
        return query.getResultList();
    }
    @Override
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        String pagingQuery = String.format("select * from (%s) t limit %d offset %d"
                , queryDto.getSql(), pageable.getPageSize(), pageable.getOffset());
        Query query = em.createNativeQuery(pagingQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return getRecords(query, pageable, count(queryDto));
    }

    //---------------------------------------------------------------------
    private List<QueryResult> getRecords(Query query) {
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

    private long count(QueryDto queryDto) {
        StringBuffer sb = new StringBuffer();
        if (queryDto.getTable() == null) {
            sb.append(String.format("select count(*) from (%s) t", queryDto.getSql()));
        } else {
            sb.append("select count(*) from " + queryDto.getTable().getFrom())
                    .append(" where " + queryDto.getTable().getWhere());
        }

        Query query = em.createNativeQuery(sb.toString());
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return query.getFirstResult();
    }
}
