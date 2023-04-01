package me.kalpha.jdbctemplate.query.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TableVo {
    private String systemId;
    private String sql;
    private Object[] params;
    private String userId;
    private Table table;
    @Builder.Default
    private Long limit = 10L;
    @Builder.Default
    private LocalDateTime requiredTime = LocalDateTime.now();

    public void updateSqlFromTable() {
        StringBuffer sb = new StringBuffer(String.format("select %s from %s", table.getSelect(), table.getFrom()));
        if (table.getWhere() != null) {
            sb.append(" where " + table.getWhere());
        }
        if (table.getOrderBy() != null) {
            sb.append(" order by " + table.getOrderBy());
        }
        sql = sb.toString();
    }
}
