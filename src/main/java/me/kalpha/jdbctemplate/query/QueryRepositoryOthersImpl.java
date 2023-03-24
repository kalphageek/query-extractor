package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.query.dto.QueryDto;
import me.kalpha.jdbctemplate.query.dto.QueryResult;
import me.kalpha.jdbctemplate.query.dto.SamplesDto;
import me.kalpha.jdbctemplate.query.dto.TableDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 생성자로 전달받은 Named EntityManager를 이용해 원하는 DB에 접속한다
 */
public class QueryRepositoryOthersImpl implements QueryRepository {

    private EntityManager em;
    public QueryRepositoryOthersImpl(EntityManager em) {
        this.em = em;
    }

    /**
     * 1개 Row만 Return 받을때는 rowList.stream().findFirst()또는 findAny() 사용.
     * 두 메소드는 해당 스트림에서 첫 번째 요소를 참조하는 Optional 객체를 반환
     * @param queryDto
     * @return
     */
    @Override
    public Boolean validateSql(QueryDto queryDto) {
        String valdateSql =
                "select (exists (select 1)\n" +
                        "        or\n" +
                        "        exists (%s)\n" +
                        ") as result";
        valdateSql = String.format(valdateSql, queryDto.getSql());
        Query query = em.createNativeQuery(valdateSql);
        if (queryDto.getParams() != null && queryDto.getParams().length != 0) {
            for (int i = 0; i < queryDto.getParams().length; i++) {
                query.setParameter(i + 1, queryDto.getParams()[i]);
            }
        }
        return (Boolean) query.getSingleResult();
    }

    @Override
    public List<QueryResult> findSamples(SamplesDto samplesDto) {
        String samplesSql = String.format("select * from %s limit %d", samplesDto.getTable(), Constants.SAMPLES_COUNT);
        Query query = em.createNativeQuery(samplesSql);
        return getRecords(query);
    }

    @Override
    public List<Object[]> findTable(TableDto tableDto) {
        Query query = em.createNativeQuery(tableDto.getSql());
        if (tableDto.getParams() != null && tableDto.getParams().length != 0) {
            for (int i = 0; i < tableDto.getParams().length; i++) {
                query.setParameter(i + 1, tableDto.getParams()[i]);
            }
        }
        return query.getResultList();
    }

    @Override
    public List<QueryResult> findTable(TableDto tableDto, Long limit) {
        String sql = String.format("%s limit %d"
                , tableDto.getSql(), tableDto.getLimit());
        Query query = em.createNativeQuery(sql);
        if (tableDto.getParams() != null && tableDto.getParams().length != 0) {
            for (int i = 0; i < tableDto.getParams().length; i++) {
                query.setParameter(i + 1, tableDto.getParams()[i]);
            }
        }
        return getRecords(query);
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, TableDto tableDto) {
        String sql = String.format("%s limit %d offset %d"
                , tableDto.getSql(), pageable.getPageSize(), pageable.getOffset());
        Query query = em.createNativeQuery(sql);
        if (tableDto.getParams() != null && tableDto.getParams().length != 0) {
            for (int i = 0; i < tableDto.getParams().length; i++) {
                query.setParameter(i + 1, tableDto.getParams()[i]);
            }
        }
        return getRecords(query, pageable, count(tableDto));
    }

    @Override
    public List<Object[]> findByQuery(QueryDto queryDto) {
        Query query = em.createNativeQuery(queryDto.getSql());
        if (queryDto.getParams() != null && queryDto.getParams().length != 0) {
            for (int i = 0; i < queryDto.getParams().length; i++) {
                query.setParameter(i + 1, queryDto.getParams()[i]);
            }
        }
        return query.getResultList();
    }
    @Override
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        String pagingQuery = String.format("select * from (%s) t limit %d offset %d"
                , queryDto.getSql(), pageable.getPageSize(), pageable.getOffset());
        Query query = em.createNativeQuery(pagingQuery);
        if (queryDto.getParams() != null && queryDto.getParams().length != 0) {
            for (int i = 0; i < queryDto.getParams().length; i++) {
                query.setParameter(i + 1, queryDto.getParams()[i]);
            }
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
        if (tableDto.getParams() != null && tableDto.getParams().length != 0) {
            for (int i = 0; i < tableDto.getParams().length; i++) {
                query.setParameter(i + 1, tableDto.getParams()[i]);
            }
        }
        return query.getFirstResult();
    }

    private long count(QueryDto queryDto) {
        String sql = String.format("select count(*) from (%s) t", queryDto.getSql());

        Query query = em.createNativeQuery(sql);
        if (queryDto.getParams() != null && queryDto.getParams().length != 0) {
            for (int i = 0; i < queryDto.getParams().length; i++) {
                query.setParameter(i + 1, queryDto.getParams()[i]);
            }
        }
        return query.getFirstResult();
    }
}
