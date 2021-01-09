package me.kalpha.jdbctemplete.entity;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Row {
    Integer rownum;
    List cols;
}
