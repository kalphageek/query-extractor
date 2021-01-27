package me.kalpha.jdbctemplate.query;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class QueryValidator {
    private final QueryService queryService;
    public QueryValidator(QueryService queryService) {
        this.queryService = queryService;
    }
    public void validateQuery(QueryDto queryDto, Errors errors) {
        try {
            queryService.validateQuery(queryDto);
        } catch (Exception e) {
            errors.rejectValue("sql", "Wrong SQL", e.getMessage());
        }
    }

}
