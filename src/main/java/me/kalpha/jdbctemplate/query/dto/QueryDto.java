package me.kalpha.jdbctemplate.query.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@Builder
public class QueryDto {
    private String systemId;
    @NonNull
    private String sql;
    private Object[] params;
    private String userId;
    @Builder.Default
    private Long limit = 10L;
    @Builder.Default
    private LocalDateTime requiredTime = LocalDateTime.now();
}
