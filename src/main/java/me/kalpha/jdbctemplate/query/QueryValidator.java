package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.domain.QueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class QueryValidator implements Validator {
    private final QueryService queryService;

    @Autowired
    public QueryValidator(QueryService queryService) {
        this.queryService = queryService;
    }

    /**
     * 인스턴스가 검증 대상 타입인지 확인
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return QueryService.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        try {
            queryService.validateSql((QueryDto) o);
        } catch (Exception e) {
            errors.rejectValue("sql", "Wrong SQL", e.getMessage());
        }
    }
}
