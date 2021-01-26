package me.kalpha.jdbctemplate.query;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class JpqQueryValidator {
    private final JpqlQueryService jpqlQueryService;
    public JpqQueryValidator(JpqlQueryService jpqlQueryService) {
        this.jpqlQueryService = jpqlQueryService;
    }
    public void validateQuery(QueryDto queryDto, Errors errors) {
        try {
            jpqlQueryService.validateQuery(queryDto);
        } catch (Exception e) {
            errors.rejectValue("sql", "Wrong SQL", e.getMessage());
        }
    }

}
