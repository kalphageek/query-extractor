package me.kalpha.jdbctemplate.query.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomRepository<T> {

    @Autowired
    private EntityManager em;

    private ObjectMapper mapper = new ObjectMapper();

    public List<T> getResultOfQuery(String argQueryString, Class<T> valueType) {
        try {
            Query query = em.createNativeQuery(argQueryString);
            NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
            nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
            List<Map<String,Object>> result = nativeQuery.getResultList();
            List<T> resultList = result.stream()
                    .map(o -> {
                        try {
                            return mapper.readValue(mapper.writeValueAsString(o),valueType);
                        } catch (Exception e) {
                            log.error(e.getMessage(),e);
                        }
                        return null;
                    }).collect(Collectors.toList());
            return resultList;
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            throw ex;
        }
    }
}