package me.kalpha.jdbctemplate.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class QueryDto {
    String dbType;
    String query;
    Object[] params;
}
