package me.kalpha.jdbctemplete.entity;

import lombok.*;

import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Row {
    Integer rownum;
    Map cols;
}
