package me.kalpha.jdbctemplate.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class JpqlQueryService {
    private final JpqlQueryRepositoryOthersImpl jpqlQueryRepositoryOthers;

    @Autowired
    public JpqlQueryService(JpqlQueryRepositoryOthersImpl jpqlQueryRepositoryOthers) {
        this.jpqlQueryRepositoryOthers = jpqlQueryRepositoryOthers;
    }

    private JpqlQueryRepository jpqlQueryRepository;

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

    private String getDBType(String systemId, String tableName) {
        return "OTHERS";
    }

    public Page<QueryResult> findSample(Pageable pageable, String tableName) {
        String systemId = "100";
        setDbType(systemId, tableName);
        return jpqlQueryRepository.findSample(pageable, tableName);
    }

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
}
