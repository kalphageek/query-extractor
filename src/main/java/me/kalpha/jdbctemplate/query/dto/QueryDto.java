package me.kalpha.jdbctemplate.query.dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@Builder
public class QueryDto {
    private String systemId;
    private String dbType;
    @NonNull
    private String sql;
    private Object[] params;
    private String userId;
}
