package me.kalpha.jdbctemplate.query.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.query.GenerateTestData;
import me.kalpha.jdbctemplate.query.dto.QueryDto;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@SpringBootTest
class QueryRepositoryTest {
    @Autowired
    @Qualifier(Constants.SYS_BATCH)
    EntityManager batchEntityManager;

    @Test
    public void queryMetaDataTest() {
        ObjectMapper mapper = new ObjectMapper();
        QueryDto queryDto = GenerateTestData.generateQueryDto();

        String pagingQuery = String.format("select * from (%s) t limit %d"
                , queryDto.getSql(), queryDto.getLimit());
        Query query = batchEntityManager.createNativeQuery(pagingQuery);

        if (queryDto.getParams() != null && queryDto.getParams().length != 0) {
            for (int i = 0; i < queryDto.getParams().length; i++) {
                query.setParameter(i + 1, queryDto.getParams()[i]);
            }
        }

        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        List<Map<String,Object>> result = nativeQuery.getResultList();

//        List<T> resultList = result.stream()
//                .map(o -> {
//                    try {
//                        return mapper.readValue(mapper.writeValueAsString(o),valueType);
//                    } catch (Exception e) {
//                        ApplicationLogger.logger.error(e.getMessage(),e);
//                    }
//                    return null;
//                }).collect(Collectors.toList());
    }
}