package kr.codesquad.secondhand.documentation.wishitem;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import kr.codesquad.secondhand.application.wishitem.WishItemService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.presentation.dto.wishitem.WishItemCategoryResponse;
import kr.codesquad.secondhand.presentation.dto.wishitem.WishItemCategoryResponses;
import kr.codesquad.secondhand.presentation.support.converter.IsWish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class WishItemDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private WishItemService wishItemService;

    @DisplayName("관심상품 등록 / 해제")
    @Test
    void changeWishStatusOfItem() throws Exception {
        // given
        willDoNothing().given(wishItemService).changeWishStatusOfItem(anyLong(), anyLong(), any(IsWish.class));

        // when
        var response = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/wishes/{itemId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .param("wish", "yes"));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."));

        // docs
        resultActions
                .andDo(document("wishitem/change",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("상품 아이디")
                        ),
                        requestParameters(
                                parameterWithName("wish").description("관심상품 등록 여부 (yes = 등록, no = 해제)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").ignored()
                        )
                ));
    }

    @DisplayName("모든 관심 상품 내역 조회")
    @Test
    void readAllWishItems() throws Exception {
        // given
        LocalDateTime createdAt = LocalDateTime.now();
        ItemResponse itemResponse = new ItemResponse(1L,
                "thumbnail-url",
                "선풍기",
                "역삼1동",
                createdAt,
                10000L,
                ItemStatus.ON_SALE,
                "seller",
                1,
                10);
        given(wishItemService.readAll(anyLong(), anyLong(), anyLong(), anyInt())).willReturn(new CustomSlice<>(
                List.of(itemResponse), 11L, true
        ));

        // when
        var response = mockMvc.perform(request(HttpMethod.GET, "/api/wishes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .param("categoryId", "1")
                .param("cursor", "1")
                .param("pageSize", "10"));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.contents[*].itemId").value(1))
                .andExpect(jsonPath("data.contents[*].thumbnailUrl").value("thumbnail-url"))
                .andExpect(jsonPath("data.contents[*].title").value("선풍기"))
                .andExpect(jsonPath("data.contents[*].tradingRegion").value("역삼1동"))
                .andExpect(jsonPath("data.contents[*].createdAt").value(createdAt.toString()))
                .andExpect(jsonPath("data.contents[*].price").value(10000))
                .andExpect(jsonPath("data.contents[*].status").value("판매중"))
                .andExpect(jsonPath("data.contents[*].sellerId").value("seller"))
                .andExpect(jsonPath("data.contents[*].chatCount").value(1))
                .andExpect(jsonPath("data.contents[*].wishCount").value(10))
                .andExpect(jsonPath("data.paging.nextCursor").value(11))
                .andExpect(jsonPath("data.paging.hasNext").value(true));

        // docs
        resultActions
                .andDo(document("wishitem/readAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                        ),
                        requestParameters(
                                parameterWithName("categoryId").description("조회하고자하는 카테고리 아이디, 명시하지 않을 시 모든 카테고리"),
                                parameterWithName("cursor").description("다음 조회할 관심상품의 아이디, 명시하지 않을 시 최초 상품부터 시작"),
                                parameterWithName("pageSize").description("조회시 페이지 크기, 명시하지 않을 시 10")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data.contents[*].itemId").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("data.contents[*].thumbnailUrl").type(STRING).description("상품 아이디"),
                                fieldWithPath("data.contents[*].title").type(STRING).description("상품 아이디"),
                                fieldWithPath("data.contents[*].tradingRegion").type(STRING).description("상품 아이디"),
                                fieldWithPath("data.contents[*].createdAt").type(STRING).description("상품 아이디"),
                                fieldWithPath("data.contents[*].price").type(NUMBER).description("상품 아이디"),
                                fieldWithPath("data.contents[*].status").type(STRING).description("상품 판매 상태"),
                                fieldWithPath("data.contents[*].sellerId").type(STRING).description("판매자"),
                                fieldWithPath("data.contents[*].chatCount").type(NUMBER).description("상품 채팅 개수"),
                                fieldWithPath("data.contents[*].wishCount").type(NUMBER).description("상품 관심 수"),
                                fieldWithPath("data.paging.nextCursor").type(NUMBER).description("다음 상품 조회 아이디"),
                                fieldWithPath("data.paging.hasNext").type(BOOLEAN).description("다음 조회할 상품이 존재하는지 여부")
                        )
                ));
    }


    @DisplayName("관심상품에 해당하는 카테고리 목록 조회")
    @Test
    void readAllCategoriesOfWishItems() throws Exception {
        // given
        given(wishItemService.readCategories(anyLong()))
                .willReturn(new WishItemCategoryResponses(
                        List.of(new WishItemCategoryResponse(1L, "가전잡화")
                        )));

        // when
        var response = mockMvc.perform(request(HttpMethod.GET, "/api/wishes/categories")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L)));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.categories[*].categoryId").isNotEmpty())
                .andExpect(jsonPath("data.categories[*].categoryName").isNotEmpty());

        // docs
        resultActions.andDo(document("wishitem/categories",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.categories[*].categoryId").type(NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.categories[*].categoryName").type(STRING).description("카테고리 이름")
                )
        ));
    }
}
