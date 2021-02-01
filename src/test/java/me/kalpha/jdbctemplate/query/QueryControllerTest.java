package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.BaseControllerTest;
import me.kalpha.jdbctemplate.domain.QueryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QueryControllerTest extends BaseControllerTest {

    private QueryDto sampleQueryDto() {
        String select = "job_execution_id,version,job_instance_id,create_time,start_time,end_time,status,exit_code,last_updated";
        String from = "batch_job_execution";
        String where = "create_time >= to_date(?,'yyyy-MM-dd')\n" +
                "\tand create_time < to_date(?,'yyyy-MM-dd') + 1\n" +
                "\tand job_instance_id > ?\n" +
                "\tand exit_code like ?\n" +
                "\tand exit_message is not null and exit_message <> ''\n" +
                "\tand status in (%s)";
        String orderBy = "job_execution_id desc, version desc";
        //in절 -->
        List<String> inClouse = new ArrayList<>();
        inClouse.add("FAILED");
        inClouse.add("WARNNING");
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < inClouse.size(); i++) {
            queryBuilder.append("?");
            if (i != inClouse.size() - 1) queryBuilder.append(", ");
        }
        where = String.format(where, queryBuilder.toString());
        //in절 <--

        Object[] params = {"2020-10-01", "2020-10-04", 20, "%" + "FAIL" + "%", inClouse.get(0), inClouse.get(1)};

        QueryDto.Table table = QueryDto.Table.builder()
                .select(select)
                .from(from)
                .where(where)
                .orderBy(orderBy)
                .build();
        QueryDto queryDto = QueryDto.builder()
                .fileName("file_name")
                .dbType("POSTGRES")
                .systemId("100")
                .userId("2043738")
                .params(params)
                .table(table)
                .build();
        queryDto.updateSqlFromTable();

        return queryDto;
    }

    @DisplayName("정상 : Table samples 조회")
    @Test
    public void find_samples() throws Exception {
        QueryDto queryDto = sampleQueryDto();
        mockMvc.perform(get("/data/table/samples")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("table-samples",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("table-paging").description("link to table paging api"),
                                linkWithRel("table-extract").description("link to table extract api")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.queryResults[0].record").description("Query 결과 (select)")
                        ),
                        getQueryDtoFieldsSnippet()
                ))
        ;
    }

    @DisplayName("정상 : Table 조회")
    @Test
    public void find_table() throws Exception {
        QueryDto queryDto = sampleQueryDto();
        mockMvc.perform(get("/data/table")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("table-paging",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("table-samples").description("link to table samples api"),
                                linkWithRel("table-extract").description("link to table extract api")
                        ),
                        getQueryDtoFieldsSnippet()
                ))
        ;
    }

    @DisplayName("정상 : 테이블 추출")
    @Test
    public void extract_table() throws Exception {
        QueryDto queryDto = sampleQueryDto();
        mockMvc.perform(post("/data/table")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("count").exists())
                .andDo(document("table-extract",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("table-paging").description("link to table paging api"),
                                linkWithRel("table-samples").description("link to table samples api")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("count").description("Extract count")
                        ),
                        getQueryDtoFieldsSnippet()
                ))
        ;
    }

    @DisplayName("정상 : Table samples paging")
    @Test
    public void find_table_pageable() throws Exception {
        QueryDto queryDto = sampleQueryDto();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/data/table")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto))
                    .param("page","0")
                    .param("size","10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("table-paging",
                        requestParameters(
                                parameterWithName("page").description("page to retrieve, begin with and default is 1"),
                                parameterWithName("size").description("Size of the page to retrieve, default 10")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("page.number").type(JsonFieldType.NUMBER).description("The number of this page."),
                                fieldWithPath("page.size").type(JsonFieldType.NUMBER).description("The size of this page."),
                                fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER).description("The total number of pages."),
                                fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER).description("The total number of results.")
                        ),
                        getQueryDtoFieldsSnippet()
                ))
        ;
    }

    @DisplayName("정상 : Query validate")
    @Test
    public void query_validate() throws Exception {
        QueryDto queryDto = sampleQueryDto();

        mockMvc.perform(get("/data/query/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("오류 : Query validate")
    @Test
    public void query_validate_error() throws Exception {
        QueryDto queryDto = sampleQueryDto();
        queryDto.setSql(queryDto.getSql()+"aaa");

        mockMvc.perform(RestDocumentationRequestBuilders.get("/data/query/validate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors").exists())
                .andDo(document("overview-errors",
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("index").description("link to query api")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("errors[0].objectName").type("QueryDto").description("A global description of the cause of the error"),
                                fieldWithPath("errors[0].field").type("String").description("Object field name"),
                                fieldWithPath("errors[0].code").type("String").description("Type of the cause of the error"),
                                fieldWithPath("errors[0].rejectedValue").type("String").description("Raised value of the error")
                        )
                ))
        ;
    }

    @DisplayName("정상 : Query 조회 paging")
    @Test
    public void query_pageable() throws Exception {
        QueryDto queryDto = sampleQueryDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/data/query")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto))
                    .param("page","0")
                    .param("size","10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryResults[0].record").exists())
                .andDo(document("query-paging",
                        requestParameters(
                                parameterWithName("page").description("page to retrieve, begin with and default is 1"),
                                parameterWithName("size").description("Size of the page to retrieve, default 10")
                        ),
                        getQueryDtoFieldsSnippet(),
                        relaxedResponseFields(
                                fieldWithPath("page.number").type(JsonFieldType.NUMBER).description("The number of this page."),
                                fieldWithPath("page.size").type(JsonFieldType.NUMBER).description("The size of this page."),
                                fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER).description("The total number of pages."),
                                fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER).description("The total number of results.")
                        )
                ))
        ;
    }

    @DisplayName("정상 : Query 추출")
    @Test
    public void query_extract() throws Exception {
        QueryDto queryDto = sampleQueryDto();

        mockMvc.perform(post("/data/query")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("count").exists())
                .andDo(document("query-extract",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("query-paging").description("link to paging query api")
                        ),
                        getQueryDtoFieldsSnippet(),
                        relaxedResponseFields(
                                fieldWithPath("count").description("Extract count")
                        )
                ))
        ;
    }


    private RequestFieldsSnippet getQueryDtoFieldsSnippet() {
        return requestFields(
                fieldWithPath("dbType").description("조회하려는 DB 타입. ex) ORACLE, GPDB, DB2, HIVE, IMPALA"),
                fieldWithPath("sql").description("Bind Variable을 갖는 SQL"),
                fieldWithPath("params").description("Bind Variable을 위한 파라미터 배열"),
                fieldWithPath("userId").description("사용자 사번"),
                fieldWithPath("systemId").description("데이터를 조회하는 시스템ID"),
                fieldWithPath("fileName").description("추출 파일명"),
                fieldWithPath("table.select").description("[select]제외한 select절"),
                fieldWithPath("table.from").description("[from]제외한 from절"),
                fieldWithPath("table.where").description("[where]제외한 where절"),
                fieldWithPath("table.orderBy").description("[order by]제외한 order by절")
        );
    }
}
