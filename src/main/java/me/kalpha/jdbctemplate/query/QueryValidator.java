package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.domain.QueryDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class QueryValidator {
    private final QueryService queryService;
    public QueryValidator(QueryService queryService) {
        this.queryService = queryService;
    }
    public void validateSql(QueryDto queryDto, Errors errors) {
        try {
            queryService.validateSql(queryDto);
        } catch (Exception e) {
            errors.rejectValue("sql", "Wrong SQL", e.getMessage());
        }
    }

}
