package me.kalpha.jdbctemplate.query.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SampleResponse {
    private String systemId;
    private String table;
    private List records;
}
