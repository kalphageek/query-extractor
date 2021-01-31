package me.kalpha.jdbctemplate.domain;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryDto {
    private String systemId;
    private String dbType;
    private String sql;
    private Object[] params;
    private String userId;
    private Table table;
    private String fileName;

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

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Table {
        private String select;
        private String from;
        private String where;
        private String orderBy;
    }
}
