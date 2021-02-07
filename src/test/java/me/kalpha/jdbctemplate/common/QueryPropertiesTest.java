package me.kalpha.jdbctemplate.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class QueryPropertiesTest {
    @Autowired
    QueryProperties queryProperties;

    @Test
    public void samplesCount() {
        System.out.println(queryProperties.getSamplesCount());
        assertTrue(queryProperties.getSamplesCount()>0);
    }
}