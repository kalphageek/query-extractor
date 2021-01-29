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
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class QueryRepositoryOracleImpl implements QueryRepository {
    @Autowired
    EntityManager em;

    @Override
    public List<Object[]> findSample(String tableName) {
        String sql = String.format("select t.* from %s t where rownum <= %d"
                , tableName, DEFAULT_LIMITS);
        Query query = em.createNativeQuery(sql);
        return query.getResultList();
    }

    @Override
    public Page<QueryResult> findSample(Pageable pageable, String tableName) {
        Integer start = (pageable.getPageNumber() - 1) * pageable.getPageSize();
        Integer end = pageable.getPageNumber() * pageable.getPageSize();
        String pagingQuery = String.format(
                "select *\n" +
                "  from (select t2.*, rownum rnum\n" +
                "          from (select t.*, count(1) over()\n" +
                "                  from %s t\n" +
                "               ) t2\n" +
                "         where rownum <= %d)\n" +
                " where rnum > %d"
                , tableName, end, start);
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
        String countQuery = String.format("select count(*) from %s", tableName);
        Query query = em.createNativeQuery(countQuery);
        return query.getFirstResult();
    }

    private int count(QueryDto queryDto) {
        String countQuery = String.format("select count(*) from (%s) t", queryDto.getSql());
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
                "select 1 from dual\n" +
                " where exists (select 1 from dual)\n" +
                "       or\n" +
                "       exists (select * from (%s))", queryDto.getSql());
        Query query = em.createNativeQuery(valdateQuery);
        for (int i=0; i<queryDto.getParams().length; i++) {
            query.setParameter(i+1, queryDto.getParams()[i]);
        }
        Optional<Integer> valid = query.getResultList().stream().findAny();
        return valid.isPresent();
    }

    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        Integer start = (pageable.getPageNumber() - 1) * pageable.getPageSize();
        Integer end = pageable.getPageNumber() * pageable.getPageSize();
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
}