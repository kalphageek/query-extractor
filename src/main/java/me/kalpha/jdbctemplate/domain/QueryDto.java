package me.kalpha.jdbctemplate.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class QueryDto {
    String dbType;
    String sql;
    Object[] params;
    String userId;
}