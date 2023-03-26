package me.kalpha.jdbctemplate.query;

import me.kalpha.jdbctemplate.common.BaseControllerTest;
import me.kalpha.jdbctemplate.query.dto.SamplesDto;
import me.kalpha.jdbctemplate.query.dto.TableDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TableControllerTest extends BaseControllerTest {

    @DisplayName("정상 : Table samples 조회. dbType과 from만 설정")
    @Test
    public void find_sample() throws Exception {
        SamplesDto samplesDto = GenerateTestData.generateSamplesDto();

        mockMvc.perform(get("/data/table/sample")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(samplesDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("table-sample",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("table-limit").description("link to table api"),
                                linkWithRel("table-paging").description("link to table paging api"),
                                linkWithRel("table-extract").description("link to table extract api")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.queryResults[0].record").description("Query 결과 (select)")
                        ),
                        getSampleFieldsSnippet()
                ))
        ;
    }

    @DisplayName("정상 : Table unpaging")
    @Test
    public void find_table() throws Exception {
        TableDto tableDto = GenerateTestData.generateTableDto();

        mockMvc.perform(get("/data/table")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tableDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("table-limit",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("table-extract").description("link to table extract api"),
                                linkWithRel("table-paging").description("link to table paging api"),
                                linkWithRel("table-sample").description("link to table sample api")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.queryResults[0].record").type("List").description("Table Result 리스트")
                        ),
                        getTableFieldsSnippet()
                ))
        ;
    }

    @DisplayName("정상 : Table 추출")
    @Test
    public void extract_table() throws Exception {
        TableDto tableDto = GenerateTestData.generateTableDto();

        mockMvc.perform(post("/data/table/extract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tableDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("count").exists())
                .andDo(document("table-extract",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("table-limit").description("link to table api"),
                                linkWithRel("table-paging").description("link to table paging api"),
                                linkWithRel("table-sample").description("link to table sample api")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("count").description("Extract count")
                        ),
                        getTableFieldsSnippet()
                ))
        ;
    }

    @DisplayName("정상 : Table paging")
    @Test
    public void find_table_pageable() throws Exception {
        TableDto tableDto = GenerateTestData.generateTableDto();

        mockMvc.perform(RestDocumentationRequestBuilders.get("/data/table/paging?page=0&size=10")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tableDto))
//                    .param("page","0")
//                    .param("size","10")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("table-paging",
                        //links.adoc 생성
                        links(halLinks(),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("self").description("link to self api"),
                                linkWithRel("table-limit").description("link to table api"),
                                linkWithRel("table-extract").description("link to table extract api"),
                                linkWithRel("table-sample").description("link to table sample api")
                        ),
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
                        getTableFieldsSnippet()
                ))
        ;
    }

    private RequestFieldsSnippet getSampleFieldsSnippet() {
        return relaxedRequestFields(
                fieldWithPath("systemId").description("데이터를 조회하는 시스템ID"),
                fieldWithPath("table").description("테이블명")
        );
    }

    private RequestFieldsSnippet getTableFieldsSnippet() {
        return relaxedRequestFields(
                fieldWithPath("params").description("Bind Variable을 위한 파라미터 배열"),
                fieldWithPath("userId").description("사용자 사번"),
                fieldWithPath("systemId").description("Catalog의 시스템ID"),
                fieldWithPath("table.select").description("[select]제외한 select절"),
                fieldWithPath("table.from").description("[from]제외한 from절"),
                fieldWithPath("table.where").description("[where]제외한 where절"),
                fieldWithPath("table.orderBy").description("[order by]제외한 order by절")
        );
    }
}
