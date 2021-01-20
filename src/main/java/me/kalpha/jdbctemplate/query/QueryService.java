package me.kalpha.jdbctemplate.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.sql.DataSource;
import java.util.List;

@Service
public class QueryService {
    private final QueryRepositoryOthersImpl queryRepositoryOthers;
    private final QueryRepositoryOracleImpl queryRepositoryOracle;

    @Autowired
    public QueryService(QueryRepositoryOracleImpl queryRepositoryOracle
            , QueryRepositoryOthersImpl queryRepositoryOthers) {
        this.queryRepositoryOracle = queryRepositoryOracle;
        this.queryRepositoryOthers = queryRepositoryOthers;
    }

    private QueryRepository queryRepository;

    public List query(QueryDto queryDto) {
        /**
         * Strategy Pattern
         */
        setDbType(queryDto.getDbType());
        return  queryRepository.findByParams(queryDto.getQuery(), queryDto.getParams());
    }

    public Page<List> query(Pageable pageable, QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return  queryRepository.findByParams(pageable, queryDto.getQuery(), queryDto.getParams());
    }

    public Boolean validate(QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return  queryRepository.validateQueryByParams(queryDto.getQuery(), queryDto.getParams());
    }

    public List findSample(String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return queryRepository.findSample(tableName);
    }

    public Page<List> findSample(Pageable pageable, String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return queryRepository.findSample(pageable, tableName);
    }

    public List findRecently(String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return queryRepository.findRecently(tableName);
    }

    public Page<List> findRecently(Pageable pageable, String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return queryRepository.findRecently(pageable, tableName);
    }

    //----------------------------------------------------------------------------------
    private String  getDBType(String tableName) {
        return "ORACLE";
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

    private void setDbType(String systemId, String tableName) {
        String dbType = getDBType(systemId, tableName);

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

}
