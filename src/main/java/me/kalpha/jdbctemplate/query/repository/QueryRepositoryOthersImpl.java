package me.kalpha.jdbctemplate.query.repository;

import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.query.dto.*;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    public List<Map<String,Object>> findSamples(SamplesDto samplesDto, Integer samplesCount) {
        String samplesSql = String.format("select * from %s limit %d", samplesDto.getTable(), samplesCount);
        Query query = em.createNativeQuery(samplesSql);

        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String,Object>> result = nativeQuery.getResultList();

        return result;
    }

//    @Override
//    public List<Object[]> findTable(TableVo tableVo) {
//        Query query = em.createNativeQuery(tableVo.getSql());
//        if (tableVo.getParams() != null && tableVo.getParams().length != 0) {
//            for (int i = 0; i < tableVo.getParams().length; i++) {
//                query.setParameter(i + 1, tableVo.getParams()[i]);
//            }
//        }
//        return query.getResultList();
//    }

    @Override
    public Object[] findTable(TableVo tableVo) throws SQLException {
        Query query = em.createNativeQuery(tableVo.getSql());
        if (tableVo.getParams() != null && tableVo.getParams().length != 0) {
            for (int i = 0; i < tableVo.getParams().length; i++) {
                query.setParameter(i + 1, tableVo.getParams()[i]);
            }
        }
        List records = query.getResultList();

        List<String> columnNames = new ArrayList<>();
        ResultSet resultSet = (ResultSet) query.unwrap(ResultSet.class);
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnNames.add(metaData.getColumnName(i));
        }

        List result = new ArrayList<>();
        result.add(columnNames);
        result.add(records);

        return result.toArray();
    }

    @Override
    public List<QueryResult> findTable(TableVo tableVo, Long limit) {
        String sql = String.format("%s limit %d"
                , tableVo.getSql(), tableVo.getLimit());
        Query query = em.createNativeQuery(sql);
        if (tableVo.getParams() != null && tableVo.getParams().length != 0) {
            for (int i = 0; i < tableVo.getParams().length; i++) {
                query.setParameter(i + 1, tableVo.getParams()[i]);
            }
        }
        return getRecords(query);
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, TableVo tableVo) {
        String sql = String.format("%s limit %d offset %d"
                , tableVo.getSql(), pageable.getPageSize(), pageable.getOffset());
        Query query = em.createNativeQuery(sql);
        if (tableVo.getParams() != null && tableVo.getParams().length != 0) {
            for (int i = 0; i < tableVo.getParams().length; i++) {
                query.setParameter(i + 1, tableVo.getParams()[i]);
            }
        }
        return getRecords(query, pageable, count(tableVo));
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
    public List<QueryResult> findByQuery(QueryDto queryDto, Long limit) {
        String pagingQuery = String.format("select * from (%s) t limit %d"
                , queryDto.getSql(), queryDto.getLimit());
        Query query = em.createNativeQuery(pagingQuery);
        if (queryDto.getParams() != null && queryDto.getParams().length != 0) {
            for (int i = 0; i < queryDto.getParams().length; i++) {
                query.setParameter(i + 1, queryDto.getParams()[i]);
            }
        }
        return getRecords(query);
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

    private long count(TableVo tableVo) {
        String sql = String.format("select count(*) from %s where %s"
                , tableVo.getTable().getFrom() , tableVo.getTable().getWhere());

        Query query = em.createNativeQuery(sql);
        if (tableVo.getParams() != null && tableVo.getParams().length != 0) {
            for (int i = 0; i < tableVo.getParams().length; i++) {
                query.setParameter(i + 1, tableVo.getParams()[i]);
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
