package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.Constants;
import me.kalpha.jdbctemplate.query.dto.*;

import java.util.ArrayList;
import java.util.List;

public class GenerateTestData {
    public static QueryDto generateQueryDto() {
//        String sql = "select job_execution_id,version,job_instance_id,create_time,start_time,end_time,status,exit_code,last_updated\n" +
//                "  from batch_job_execution\n" +
//                " where create_time >= to_date(?,'yyyy-MM-dd')\n" +
//                "   and create_time < to_date(?,'yyyy-MM-dd') + 1\n" +
//                "   and job_instance_id > ?\n" +
//                "   and exit_code like ?\n" +
//                "   and exit_message is not null and exit_message <> ''\n" +
//                "   and status in (%s)\n" +
//                " order by job_execution_id desc, version desc";
        String sql = "select member.id, age, member.created_at, username, team_id, team.name\n" +
                "  from member\n" +
                "  join team on member.team_id = team.id\n" +
                " where age > ?\n" +
                "   and username like ?\n" +
                "   and team.name in (%s)\n" +
                " order by team_id, member.id";
        //in절 -->
        List<String> inClouse = new ArrayList<>();
        inClouse.add("teamA");
        inClouse.add("teamB");
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < inClouse.size(); i++) {
            queryBuilder.append("?");
            if (i != inClouse.size() - 1) queryBuilder.append(", ");
        }
        sql = String.format(sql, queryBuilder.toString());
        //in절 <--

//        Object[] params = {"2020-10-01", "2020-10-04", 20, "%" + "FAIL" + "%", inClouse.get(0), inClouse.get(1)};
        Object[] params = {20, "member%", inClouse.get(0), inClouse.get(1)};

        QueryDto queryDto = QueryDto.builder()
                .systemId(Constants.BATCH_UNIT_NAME)
                .userId("2043738")
                .params(params)
                .sql(sql)
                .build();
        return queryDto;
    }

    public static TableDto generateTableDto() {
//        String select = "job_execution_id,version,job_instance_id,create_time,start_time,end_time,status,exit_code,last_updated";
//        String from = "batch_job_execution";
//        String where = "create_time >= to_date(?,'yyyy-MM-dd')\n" +
//                "   and create_time < to_date(?,'yyyy-MM-dd') + 1\n" +
//                "   and job_instance_id > ?\n" +
//                "   and exit_code like ?\n" +
//                "   and exit_message is not null and exit_message <> ''\n" +
//                "   and status in (%s)\n";
//        String orderBy = "job_execution_id desc, version desc";
        String select = "id, age, created_at, username, team_id";
        String from = "member";
        String where = "age > ?\n" +
                "   and username like ?\n" +
                "   and team_id in (%s)";
        String orderBy = "team_id, id";
        //in절 -->
        List<String> inClouse = new ArrayList<>();
        inClouse.add("1");
        inClouse.add("2");
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < inClouse.size(); i++) {
            queryBuilder.append("?");
            if (i != inClouse.size() - 1) queryBuilder.append(", ");
        }
        where = String.format(where, queryBuilder.toString());
        //in절 <--

//        Object[] params = {"2020-10-01", "2020-10-04", 20, "%" + "FAIL" + "%", inClouse.get(0), inClouse.get(1)};
        Object[] params = {20, "member%", inClouse.get(0), inClouse.get(1)};

        Table table = Table.builder()
                .select(select)
                .from(from)
                .where(where)
                .orderBy(orderBy)
                .build();
        TableDto tableDto = TableDto.builder()
                .systemId(Constants.BATCH_UNIT_NAME)
                .userId("2043738")
                .params(params)
                .table(table)
                .build();

        return tableDto;
    }

    public static SamplesDto generateSamplesDto() {
        SamplesDto samplesDto = SamplesDto.builder()
                .table("member")
                .systemId(Constants.BATCH_UNIT_NAME)
                .build();
        return samplesDto;
    }
}
