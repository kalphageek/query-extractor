package me.kalpha.jdbctemplate.batch;

import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.common.QueryProperties;
import me.kalpha.jdbctemplate.dto.QueryDto;
import me.kalpha.jdbctemplate.dto.QueryResult;
import me.kalpha.jdbctemplate.dto.SamplesDto;
import me.kalpha.jdbctemplate.dto.TableDto;
import me.kalpha.jdbctemplate.query.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class QueryRepositoryOthersImpl implements QueryRepository {

    @Autowired
    private QueryProperties queryProperties;

    /**
     * BATCH DB 접속
     */
    @Autowired
    @PersistenceContext(unitName = Constants.BATCH_UNIT_NAME)
    private EntityManager em;

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
    public List<QueryResult> findSamples(SamplesDto samplesDto) {
        String samplesSql = String.format("select * from %s limit %d", samplesDto.getTable(), queryProperties.getSamplesCount());
        Query query = em.createNativeQuery(samplesSql);
        return getRecords(query);
    }

    @Override
    public List<Object[]> findTable(TableDto tableDto) {
        Query query = em.createNativeQuery(tableDto.getSql());
        for (int i = 0; i < tableDto.getParams().length; i++) {
            query.setParameter(i + 1, tableDto.getParams()[i]);
        }
        return query.getResultList();
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, TableDto tableDto) {
        String sql = String.format("%s limit %d offset %d"
                , tableDto.getSql(), pageable.getPageSize(), pageable.getOffset());
        Query query = em.createNativeQuery(sql);
        for (int i = 0; i < tableDto.getParams().length; i++) {
            query.setParameter(i + 1, tableDto.getParams()[i]);
        }
        return getRecords(query, pageable, count(tableDto));
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

    private long count(TableDto tableDto) {
        String sql = String.format("select count(*) from %s where %s"
                ,tableDto.getTable().getFrom() ,tableDto.getTable().getWhere());

        Query query = em.createNativeQuery(sql);
        for (int i=0; i<tableDto.getParams().length; i++) {
            query.setParameter(i+1, tableDto.getParams()[i]);
        }
        return query.getFirstResult();
    }

    private long count(QueryDto queryDto) {
        String sql = String.format("select count(*) from (%s) t", queryDto.getSql());

        Query query = em.createNativeQuery(sql);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return query.getFirstResult();
    }
}
