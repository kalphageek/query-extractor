package me.kalpha.jdbctemplete.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class QueryDto {
    String query;
    Object[] params;
}
