package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.domain.QueryDto;
import me.kalpha.jdbctemplate.domain.QueryResult;
import me.kalpha.jdbctemplate.domain.SamplesDto;
import me.kalpha.jdbctemplate.domain.TableDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryServiceImpl implements QueryService {
    private final QueryRepositoryOthersImpl queryRepositoryOthers;
    private final QueryRepositoryOracleImpl queryRepositoryOracle;

    private QueryRepository queryRepository;

    @Autowired
    public QueryServiceImpl(QueryRepositoryOthersImpl queryRepositoryOthers, QueryRepositoryOracleImpl queryRepositoryOracle) {
        this.queryRepositoryOthers = queryRepositoryOthers;
        this.queryRepositoryOracle = queryRepositoryOracle;
    }

    @Override
    public List<QueryResult> findSamples(SamplesDto samplesDto) {
        setDbType(samplesDto.getDbType());
        return queryRepository.findSamples(samplesDto);
    }

    @Override
    public long extractTable(TableDto tableDto) {
        List list = findTable(tableDto);
        String fileName= null;
        return saveResult(list, fileName);
    }

    private List<Object[]> findTable(TableDto tableDto) {
        setDbType(tableDto.getDbType(), tableDto.getTable().getFrom());
        return queryRepository.findTable(tableDto);
    }

    @Override
    public Page<QueryResult> findTable(Pageable pageable, TableDto tableDto) {
        setDbType(tableDto.getDbType(), tableDto.getTable().getFrom());
        return queryRepository.findTable(pageable, tableDto);
    }

    @Override
    public Boolean validateSql(QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return queryRepository.validateSql(queryDto);
    }

    @Override
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return queryRepository.findByQuery(pageable, queryDto);
    }

    @Override
    public long extractByQuery(QueryDto queryDto) {
        String fileName= null;
        return saveResult(queryRepository.findByQuery(queryDto), fileName);
    }

    private List<Object[]> findByQuery(QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return queryRepository.findByQuery(queryDto);
    }

    //-------------------------------------------------

    private void setDbType(String systemId, String tableName) {
        String dbType = getDBType(systemId, tableName);
        setDbType(dbType);
    }

    private void setDbType(String dbType) {
        switch (dbType) {
            case "ORACLE":
                queryRepository = queryRepositoryOracle;
                break;
            default:
                queryRepository = queryRepositoryOthers;
        }
    }

    private String getDBType(String systemId, String tableName) {
        return "POSTGRES";
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
