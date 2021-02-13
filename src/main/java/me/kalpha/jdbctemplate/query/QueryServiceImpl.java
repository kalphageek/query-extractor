package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.query.dto.QueryDto;
import me.kalpha.jdbctemplate.query.dto.QueryResult;
import me.kalpha.jdbctemplate.query.dto.SamplesDto;
import me.kalpha.jdbctemplate.query.dto.TableDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * {@link EntityManagerConfig}의 Named EntityManager를 Injection해서 Repository 생성자로 전달한다.
 */
@Service
public class QueryServiceImpl implements QueryService {
    EntityManager batchEntityManager;
    EntityManager ehubEntityManater;

    private QueryRepository queryRepository;
    private QueryRepositoryOracleImpl queryRepositoryOracle;
    private QueryRepositoryOthersImpl queryRepositoryOthers;

    @Autowired
    public QueryServiceImpl(@Qualifier(Constants.BATCH_UNIT_NAME) EntityManager batchEntityManager,
                            @Qualifier(Constants.EHUB_UNIT_NAME) EntityManager ehubEntityManater) {
        queryRepositoryOracle = new QueryRepositoryOracleImpl(ehubEntityManater);
        queryRepositoryOthers = new QueryRepositoryOthersImpl(batchEntityManager);
    }


    @Override
    public List<QueryResult> findSamples(SamplesDto samplesDto) {
        setRepository(samplesDto.getSystemId());
        return queryRepository.findSamples(samplesDto);
    }

    @Override
    public long extractTable(TableDto tableDto) {
        List list = findTable(tableDto);
        String fileName= null;
        return saveResult(list, fileName);
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, TableDto tableDto) {
        setRepository(tableDto.getSystemId());
        return queryRepository.findTable(pageable, tableDto);
    }

    @Override
    public Boolean validateSql(QueryDto queryDto) {
        setRepository(queryDto.getSystemId());
        return queryRepository.validateSql(queryDto);
    }

    @Override
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        setRepository(queryDto.getSystemId());
        return queryRepository.findByQuery(pageable, queryDto);
    }

    @Override
    public long extractByQuery(QueryDto queryDto) {
        String fileName= null;
        return saveResult(queryRepository.findByQuery(queryDto), fileName);
    }

    //-------------------------------------------------

    private List<Object[]> findTable(TableDto tableDto) {
        setRepository(tableDto.getSystemId());
        return queryRepository.findTable(tableDto);
    }

    private List<Object[]> findByQuery(QueryDto queryDto) {
        setRepository(queryDto.getSystemId());
        return queryRepository.findByQuery(queryDto);
    }

    private void setRepository(String systemId) {
        switch (systemId) {
            case Constants.SYS_BATCH:
                queryRepository = queryRepositoryOthers;
                break;
            case Constants.SYS_EHUB:
                queryRepository = queryRepositoryOthers;
        }
    }

    private long saveResult(List list, String filename) {
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
