package me.kalpha.jdbctemplate.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JpqlQueryServiceImpl implements JpqlQueryService {
    private final JpqlQueryRepositoryOthersImpl jpqlQueryRepositoryOthers;

    @Autowired
    public JpqlQueryServiceImpl(JpqlQueryRepositoryOthersImpl jpqlQueryRepositoryOthers) {
        this.jpqlQueryRepositoryOthers = jpqlQueryRepositoryOthers;
    }

    private JpqlQueryRepository jpqlQueryRepository;

    @Override
    public List findSample(String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return jpqlQueryRepository.findSample(tableName);
    }

    private void setDbType(String systemId, String tableName) {
        String dbType = getDBType(systemId, tableName);

        switch (dbType) {
            case "ORACLE":
                jpqlQueryRepository = jpqlQueryRepositoryOthers;
                break;
            default:
                jpqlQueryRepository = jpqlQueryRepositoryOthers;
        }
    }

    private void setDbType(String dbType) {
        switch (dbType) {
            case "ORACLE":
                jpqlQueryRepository = jpqlQueryRepositoryOthers;
                break;
            default:
                jpqlQueryRepository = jpqlQueryRepositoryOthers;
        }
    }

    private String getDBType(String systemId, String tableName) {
        return "OTHERS";
    }

    @Override
    public Page<QueryResult> findSample(Pageable pageable, String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return jpqlQueryRepository.findSample(pageable, tableName);
    }

    @Override
    public long extractSample(String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        List resultList = jpqlQueryRepository.extractSample(tableName);

        //TO-DO - Code for save resultList to Isilon
        for (int i=0; i<resultList.size(); i++) {
            Object[] ov = (Object[]) resultList.get(i);
            StringBuffer sb = new StringBuffer();
            for (int j=0; j< ov.length; j++) {
                if (j != 0) {
                    sb.append("\t" + ov[j]);
                }
                sb.append(ov[j]);
            }
            System.out.println(sb);
        }
        //

        return resultList.size();
    }

    @Override
    public Boolean validateQuery(QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return jpqlQueryRepository.validateQuery(queryDto);
    }

    @Override
    public Page<QueryResult> findByQuery(Pageable pageable, QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return jpqlQueryRepository.findQuery(pageable, queryDto);
    }

    @Override
    public long extractByQuery(QueryDto queryDto) {
        setDbType(queryDto.getDbType());
        return jpqlQueryRepository.extractQuery(queryDto);
    }
}
