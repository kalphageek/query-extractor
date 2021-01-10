package me.kalpha.jdbctemplete.domain;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@ToString
public class Row {
    Integer rownum;
    List cols;
}
