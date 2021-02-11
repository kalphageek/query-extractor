package me.kalpha.jdbctemplate.ehub;

import lombok.extern.slf4j.Slf4j;
import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.common.QueryProperties;
import me.kalpha.jdbctemplate.dto.QueryDto;
import me.kalpha.jdbctemplate.dto.QueryResult;
import me.kalpha.jdbctemplate.dto.SamplesDto;
import me.kalpha.jdbctemplate.dto.TableDto;
import me.kalpha.jdbctemplate.query.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class QueryRepositoryOracleImpl implements QueryRepository {

    @Autowired
    @PersistenceContext(unitName = Constants.CATALOG_UNIT_NAME)
    EntityManager em;

    /**
     * 1개 Row만 Return 받을때는 rowList.stream().findFirst()또는 findAny() 사용.
     * 두 메소드는 해당 스트림에서 첫 번째 요소를 참조하는 Optional 객체를 반환
     * @param queryDto
     * @return
     */
    @Override
    public Boolean validateSql(QueryDto queryDto) {
        String valdateSql =
                "select 1 from dual\n" +
                        " where exists (select 1 from dual)\n" +
                        "       or\n" +
                        "       exists (select * from (%s))";
        valdateSql = String.format(valdateSql, queryDto.getSql());
        Query query = em.createNativeQuery(valdateSql);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        Optional<Integer> valid = query.getResultList().stream().findAny();
        return valid.isPresent();
    }

    @Override
    public List<QueryResult> findSamples(SamplesDto samplesDto) {
        String samplesSql = String.format("select * from %s where rownum <= %d", samplesDto.getTable(), Constants.SAMPLES_COUNT);
        Query query = em.createNativeQuery(samplesSql);
        return getRecords(query);
    }


    @Override
    public List<Object[]> findTable(TableDto tableDto) {
        Query query = em.createNativeQuery(tableDto.getSql());
        for (int i=0; i<tableDto.getParams().length; i++) {
            query.setParameter(i+1, tableDto.getParams()[i]);
        }
        return query.getResultList();
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, TableDto tableDto) {
        Integer start = pageable.getPageNumber() * pageable.getPageSize();
        Integer end = (pageable.getPageNumber() + 1) * pageable.getPageSize();
        String pagingQuery = String.format(
                "select *\n" +
                        "  from (select t2.*, rownum rnum\n" +
                        "          from (select t.*, count(1) over()\n" +
                        "                  from %s t\n" +
                        "               ) t2\n" +
                        "         where rownum <= %d)\n" +
                        " where rnum > %d"
                , tableDto.getTable().getFrom(), end, start);
        Query query = em.createNativeQuery(pagingQuery);
        for (int i=0; i<tableDto.getParams().length; i++) {
            query.setParameter(i+1, tableDto.getParams()[i]);
        }
        return getRecords(query, pageable, count(tableDto));
    }

    @Override
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        Integer start = pageable.getPageNumber() * pageable.getPageSize();
        Integer end = (pageable.getPageNumber() + 1) * pageable.getPageSize();
        String pagingQuery = String.format(
                "select *\n" +
                        "  from (select t3.*, rownum as rnum\n" +
                        "          from (select t2.*, count(1) over()\n" +
                        "                  from (\n" +
                        "--SQL Begin--\n" +
                        "%s\n" +
                        "--SQL End--\n" +
                        "                       ) t2\n" +
                        "                ) t3\n" +
                        "          where rownum <= %d)\n" +
                        " where rnum > %d"
                , queryDto.getSql(), end, start);
        Query query = em.createNativeQuery(pagingQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
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

    //---------------------------------------------------------------------
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