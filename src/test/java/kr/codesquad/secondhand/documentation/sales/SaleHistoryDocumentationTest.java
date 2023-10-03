package kr.codesquad.secondhand.documentation.sales;

import static org.mockito.BDDMockito.anyInt;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kr.codesquad.secondhand.application.item.SalesHistoryService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class SaleHistoryDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private SalesHistoryService salesHistoryService;

    @DisplayName("판매내역 조회")
    @Test
    void readHistory() throws Exception {
        // given
        given(salesHistoryService.read(anyLong(), anyString(), anyInt(), anyLong()))
                .willReturn(FixtureFactory.createCustomSliceItemResponse());

        // when
        var response = mockMvc.perform(request(HttpMethod.GET, "/api/sales/history")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .param("status", "all")
                .param("cursor", "1")
                .param("size", "10"));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("성공했습니다."))
                .andExpect(jsonPath("$.data.contents[*].itemId").exists())
                .andExpect(jsonPath("$.data.contents[*].thumbnailUrl").exists())
                .andExpect(jsonPath("$.data.contents[*].title").exists())
                .andExpect(jsonPath("$.data.contents[*].tradingRegion").exists())
                .andExpect(jsonPath("$.data.contents[*].createdAt").exists())
                .andExpect(jsonPath("$.data.contents[*].price").exists())
                .andExpect(jsonPath("$.data.contents[*].status").exists())
                .andExpect(jsonPath("$.data.contents[*].sellerId").exists())
                .andExpect(jsonPath("$.data.contents[*].chatCount").exists())
                .andExpect(jsonPath("$.data.contents[*].wishCount").exists())
                .andExpect(jsonPath("$.data.paging.nextCursor").exists())
                .andExpect(jsonPath("$.data.paging.hasNext").exists());

        // docs
        resultActions.andDo(document("sales/readAll",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                requestParameters(
                        parameterWithName("status").description("판매 상태 (all, on_sale, sold_out)"),
                        parameterWithName("cursor").description("다음 페이지를 가져오기 위한 커서"),
                        parameterWithName("size").description("한 페이지에 가져올 상품 개수")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답 상태 코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.contents").type(ARRAY).description("응답 데이터"),
                        fieldWithPath("data.contents[].itemId").type(NUMBER).description("상품 아이디"),
                        fieldWithPath("data.contents[].thumbnailUrl").type(STRING).description("상품 썸네일 이미지"),
                        fieldWithPath("data.contents[].title").type(STRING).description("상품 제목"),
                        fieldWithPath("data.contents[].tradingRegion").type(STRING).description("상품 거래 지역"),
                        fieldWithPath("data.contents[].createdAt").type(STRING).description("상품 등록 시간"),
                        fieldWithPath("data.contents[].price").type(NUMBER).description("상품 가격"),
                        fieldWithPath("data.contents[].status").type(STRING).description("상품 판매 상태"),
                        fieldWithPath("data.contents[].sellerId").type(STRING).description("상품 판매자 아이디"),
                        fieldWithPath("data.contents[].chatCount").type(NUMBER).description("상품 채팅 개수"),
                        fieldWithPath("data.contents[].wishCount").type(NUMBER).description("상품 관심 개수"),
                        fieldWithPath("data.paging.nextCursor").type(NUMBER).description("다음 페이지를 가져오기 위한 커서"),
                        fieldWithPath("data.paging.hasNext").type(BOOLEAN).description("다음 페이지가 있는지 여부")
                )
        ));
    }
}
