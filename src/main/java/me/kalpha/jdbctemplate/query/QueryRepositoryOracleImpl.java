package me.kalpha.jdbctemplate.query;

import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class QueryRepositoryOracleImpl implements QueryRepository {
    @Autowired
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
    public List<QueryResult> findSamples(QueryDto queryDto) {
        String samplesSql = String.format("select * from %s where rownum <= %d", queryDto.getTable().getFrom(), QueryService.SAMPLES_COUNT);
        Query query = em.createNativeQuery(samplesSql);
        return getRecords(query);
    }


    @Override
    public List<Object[]> findTable(QueryDto queryDto) {
        Query query = em.createNativeQuery(queryDto.getSql());
        return query.getResultList();
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, QueryDto queryDto) {
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
                , queryDto.getTable().getFrom(), end, start);
        Query query = em.createNativeQuery(pagingQuery);
        return getRecords(query, pageable, count(queryDto));
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

    private int count(QueryDto queryDto) {
        StringBuffer sb = new StringBuffer();
        if (queryDto.getTable() == null) {
            sb.append("select count(*) from " + queryDto.getTable().getFrom())
                    .append(" where " + queryDto.getTable().getWhere());
        } else {
            sb.append(String.format("select count(*) from %s", queryDto.getSql()));
        }

        Query query = em.createNativeQuery(sb.toString());
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        return query.getFirstResult();
    }
}