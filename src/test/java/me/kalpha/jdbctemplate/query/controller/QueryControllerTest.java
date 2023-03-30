package me.kalpha.jdbctemplate.query.controller;

import me.kalpha.jdbctemplate.common.BaseControllerTest;
import me.kalpha.jdbctemplate.query.GenerateTestData;
import me.kalpha.jdbctemplate.query.dto.QueryDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class QueryControllerTest extends BaseControllerTest {
    @DisplayName("정상 : Query validate")
    @Test
    public void query_validate() throws Exception {
        QueryDto queryDto = GenerateTestData.generateQueryDto();

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
        QueryDto queryDto = GenerateTestData.generateQueryDto();
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
        QueryDto queryDto = GenerateTestData.generateQueryDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/data/query/paging")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(queryDto))
                    .param("page","0")
                    .param("size","10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryResults[0].record").exists())
                .andDo(document("query-paging",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("query-extract").description("link to extract query api"),
                                linkWithRel("query-limit").description("link to query api")
                        ),
                        requestParameters(
                                parameterWithName("page").description("page to retrieve, begin with and default is 1"),
                                parameterWithName("size").description("Size of the page to retrieve, default 10")
                        ),
                        getRequestFieldsSnippet(),
                        relaxedResponseFields(
                                fieldWithPath("page.number").type(JsonFieldType.NUMBER).description("The number of this page."),
                                fieldWithPath("page.size").type(JsonFieldType.NUMBER).description("The size of this page."),
                                fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER).description("The total number of pages."),
                                fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER).description("The total number of results.")
                        )
                ))
        ;
    }

    @DisplayName("정상 : Query 조회 Unpaging")
    @Test
    public void query() throws Exception {
        QueryDto queryDto = GenerateTestData.generateQueryDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/data/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(queryDto))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.queryResults[0].record").exists())
                .andDo(document("query-limit",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("query-paging").description("link to paging query api"),
                                linkWithRel("query-extract").description("link to extract query api")
                        ),
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("query-paging").description("link to paging query api"),
                                linkWithRel("query-extract").description("link to query extract api")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.queryResults[0].record").type("List").description("Query Result 리스트")
                        ),
                        getRequestFieldsSnippet()
                ))
        ;
    }
    @DisplayName("정상 : Query 추출")
    @Test
    public void query_extract() throws Exception {
        QueryDto queryDto = GenerateTestData.generateQueryDto();

        mockMvc.perform(post("/data/query/extract")
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
                                linkWithRel("query-paging").description("link to paging query api"),
                                linkWithRel("query-limit").description("link to query api")
                        ),
                        getRequestFieldsSnippet(),
                        relaxedResponseFields(
                                fieldWithPath("count").description("Extract count")
                        )
                ))
        ;
    }

    private RequestFieldsSnippet getRequestFieldsSnippet() {
        return relaxedRequestFields(
                fieldWithPath("sql").description("Bind Variable을 갖는 SQL"),
                fieldWithPath("params").description("Bind Variable을 위한 파라미터 배열"),
                fieldWithPath("userId").description("사용자 사번"),
                fieldWithPath("systemId").description("Catalog의 시스템ID : Batch(100), e-Hub(200)")//,
//                fieldWithPath("requiredTime").description("실행시간")
        );
    }
}
