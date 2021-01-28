package me.kalpha.jdbctemplate.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryServiceImpl implements QueryService {
    private final QueryRepositoryOthersImpl queryRepositoryOthers;
    private final QueryRepositoryOracleImpl queryRepositoryOracle;

    @Autowired
    public QueryServiceImpl(QueryRepositoryOthersImpl queryRepositoryOthers, QueryRepositoryOracleImpl queryRepositoryOracle) {
        this.queryRepositoryOthers = queryRepositoryOthers;
        this.queryRepositoryOracle = queryRepositoryOracle;
    }

    private QueryRepository queryRepository;

    @Override
    public List findSample(String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return queryRepository.findSample(tableName);
    }

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
        return "OTHERS";
    }

    @Override
    public Page<QueryResult> findSample(Pageable pageable, String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return queryRepository.findSample(pageable, tableName);
    }

    @Override
    public long extractSample(String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        String filename = null;
        return saveResult(queryRepository.findSample(tableName), filename);
    }

    @Override
    public Boolean validateQuery(QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return queryRepository.validateQuery(queryDto);
    }

    @Override
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return queryRepository.findByQuery(pageable, queryDto);
    }

    @Override
    public long extractByQuery(QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        String filename = null;
        return saveResult(queryRepository.findByQuery(queryDto), filename);
    }

    @Override
    public List findByQuery(QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return queryRepository.findByQuery(queryDto);
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
