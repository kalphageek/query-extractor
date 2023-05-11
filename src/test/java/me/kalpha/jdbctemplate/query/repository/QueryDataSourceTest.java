package me.kalpha.jdbctemplate.query.repository;

import me.kalpha.jdbctemplate.common.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.*;

@SpringBootTest
public class QueryDataSourceTest {

    @Autowired
    @Qualifier(Constants.SYS_BATCH)
    private DataSource dataSource;

    @Test
    @DisplayName("dataSource 및 metaData 조회 테스트")
    public void dataSource_metaData_Test() throws SQLException {
        String query = "SELECT * FROM member";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData rsmd1 = resultSet.getMetaData();
            int columnCount1 = rsmd1.getColumnCount();
            while (resultSet.next()) {
                // 컬럼명과 데이터 출력
                for (int i = 1; i <= columnCount1; i++) {
                    String columnName = rsmd1.getColumnName(i);
                    Object value = resultSet.getObject(i);
                    System.out.println(columnName + " : " + value);
                }
            }
            resultSet.close();
            statement.close();
        }
    }
}
