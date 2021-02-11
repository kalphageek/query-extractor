package me.kalpha.jdbctemplate.query.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class SamplesDto {
    private String systemId;
    private String dbType;
    @NonNull
    private String table;
}
