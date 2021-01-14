package me.kalpha.jdbctemplete.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

/**
 * 입력값 오류를 검증하기 위한 Class
 * 입력값 오류가 있으면 rejectValue를 사용해 에러내용을 등록한다.
 */
@Component
public class QueryValidator {
    private final QueryService queryService;

    @Autowired
    public QueryValidator(QueryService queryService) {
        this.queryService = queryService;
    }

    public void validate(QueryDto queryDto, Errors errors) {
        try {
            queryService.validate(queryDto);
        } catch (Exception e) {
            errors.rejectValue("query", "Wrong SQL", e.getMessage());
        }
    }
}
