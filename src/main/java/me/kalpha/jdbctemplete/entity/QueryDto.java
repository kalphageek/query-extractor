package me.kalpha.jdbctemplete.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class QueryDto {
    String query;
    List<String> conditions;
}
