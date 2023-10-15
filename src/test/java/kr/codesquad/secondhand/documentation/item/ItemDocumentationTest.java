package kr.codesquad.secondhand.documentation.item;

import kr.codesquad.secondhand.application.item.ItemReadFacade;
import kr.codesquad.secondhand.application.item.ItemService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemStatusRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import static kr.codesquad.secondhand.documentation.support.ConstraintsHelper.withPath;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ItemDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemReadFacade itemReadFacade;

    @DisplayName("상품 등록")
    @Test
    void registerItem() throws Exception {
        // given
        ItemRegisterRequest request = FixtureFactory.createItemRegisterRequest();

        willDoNothing()
                .given(itemService)
                .register(any(MultipartFile.class), anyList(), any(ItemRegisterRequest.class), anyLong());

        MockMultipartFile thumbnail = new MockMultipartFile("thumbnailImage", "image.png", "image/png",
                "<<png data>>".getBytes());
        MockMultipartFile itemImage = new MockMultipartFile("images", "image.png", "image/png",
                "<<png data>>".getBytes());
        MockMultipartFile itemRegisterData = new MockMultipartFile("item", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when
        var response = mockMvc.perform(multipart(HttpMethod.POST, "/api/items")
                .file(thumbnail)
                .file(itemImage)
                .file(itemRegisterData)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then
        var resultActions = response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("statusCode").value(201))
                .andExpect(jsonPath("message").value("성공했습니다."));

        // docs
        resultActions.andDo(document("items/register",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                requestParts(
                        partWithName("thumbnailImage").description("썸네일 이미지"),
                        partWithName("images").optional().description("상품 이미지")
                                .attributes(key("nullable").value(true)),
                        partWithName("item").description("상품 데이터")
                ),
                requestPartFields("item",
                        withPath("title", ItemRegisterRequest.class).description("상품 제목"),
                        withPath("price", ItemRegisterRequest.class).description("상품 가격"),
                        withPath("content", ItemRegisterRequest.class).description("상품 설명"),
                        withPath("region", ItemRegisterRequest.class).description("상품 판매 지역"),
                        withPath("status", ItemRegisterRequest.class)
                                .description("상품 판매 상태 (ON_SALE, RESERVED, SOLD_OUT)"),
                        withPath("categoryId", ItemRegisterRequest.class).description("상품 카테고리 아이디"),
                        withPath("categoryName", ItemRegisterRequest.class).description("상품 카테고리 이름")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data").ignored()
                )
        ));
    }

    @DisplayName("상품 수정")
    @Test
    void updateItem() throws Exception {
        // given
        ItemUpdateRequest request = FixtureFactory.createItemUpdateRequest();

        willDoNothing()
                .given(itemService)
                .update(any(MultipartFile.class), anyList(), any(ItemUpdateRequest.class), anyLong(), anyLong());

        MockMultipartFile thumbnail = new MockMultipartFile("thumbnailImage", "image.png", "image/png",
                "<<png data>>".getBytes());
        MockMultipartFile itemImage = new MockMultipartFile("images", "image.png", "image/png",
                "<<png data>>".getBytes());
        MockMultipartFile itemUpdateData = new MockMultipartFile("item", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when
        var builder = RestDocumentationRequestBuilders.multipart("/api/items/{itemId}", 1);
        builder.with(mockRequest -> {
            mockRequest.setMethod(HttpMethod.PATCH.name());
            return mockRequest;
        });

        var response = mockMvc.perform(builder
                .file(thumbnail)
                .file(itemImage)
                .file(itemUpdateData)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."));

        resultActions
                .andDo(document("items/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                        ),
                        pathParameters(
                                parameterWithName("itemId").description("상품 아이디")
                        ),
                        requestParts(
                                partWithName("thumbnailImage").optional().description("수정할 썸네일 이미지")
                                        .attributes(key("nullable").value(true)),
                                partWithName("images").optional().description("새롭게 추가할 상품 이미지")
                                        .attributes(key("nullable").value(true)),
                                partWithName("item").description("상품 수정 데이터")
                        ),
                        requestPartFields("item",
                                withPath("title", ItemUpdateRequest.class).attributes().description("상품 제목"),
                                withPath("price", ItemUpdateRequest.class).description("상품 가격"),
                                withPath("content", ItemUpdateRequest.class).description("상품 설명"),
                                withPath("region", ItemUpdateRequest.class).description("상품 판매 지역"),
                                withPath("status", ItemUpdateRequest.class)
                                        .description("상품 판매 상태 (ON_SALE, RESERVED, SOLD_OUT)"),
                                withPath("categoryId", ItemUpdateRequest.class).description("상품 카테고리 아이디"),
                                withPath("categoryName", ItemUpdateRequest.class).description("상품 카테고리 이름"),
                                withPath("deleteImageUrls", ItemUpdateRequest.class).description("상품 수정시 삭제할 이미지 url")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                                fieldWithPath("message").type(STRING).description("응답 메시지"),
                                fieldWithPath("data").ignored()
                        )
                ));
    }

    @DisplayName("상품 삭제")
    @Test
    void deleteItem() throws Exception {
        // given
        willDoNothing()
                .given(itemService)
                .delete(anyLong(), anyLong());

        // when
        var response = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/items/{itemId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."));

        // docs
        resultActions.andDo(document("items/delete",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                pathParameters(
                        parameterWithName("itemId").description("상품 아이디")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data").ignored()
                )
        ));
    }

    @DisplayName("상품 판매상태 변경")
    @Test
    void updateItemStatus() throws Exception {
        // given
        willDoNothing()
                .given(itemService)
                .updateStatus(any(), anyLong(), anyLong());

        // when
        var response = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/items/{itemId}/status", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(new ItemStatusRequest("SOLD_OUT"))));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."));

        // docs
        resultActions.andDo(document("items/updateStatus",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                pathParameters(
                        parameterWithName("itemId").description("상품 아이디")
                ),
                requestFields(
                        withPath("status", ItemStatusRequest.class).type(STRING)
                                .description("상품 판매 상태 (ON_SALE, RESERVED, SOLD_OUT)")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data").ignored()
                )
        ));
    }

    @DisplayName("상품 목록 조회")
    @Test
    void readAll() throws Exception {
        // given
        given(itemService.readAll(anyLong(), anyLong(), anyString(), anyInt()))
                .willReturn(FixtureFactory.createCustomSliceItemResponse());

        // when
        var response = mockMvc.perform(request(HttpMethod.GET, "/api/items")
                .param("cursor", "1")
                .param("categoryId", "1")
                .param("region", "역삼1동")
                .param("size", "10")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.contents").isArray())
                .andExpect(jsonPath("data.contents[*].itemId").exists())
                .andExpect(jsonPath("data.contents[*].thumbnailUrl").exists())
                .andExpect(jsonPath("data.contents[*].title").exists())
                .andExpect(jsonPath("data.contents[*].tradingRegion").exists())
                .andExpect(jsonPath("data.contents[*].createdAt").exists())
                .andExpect(jsonPath("data.contents[*].price").exists())
                .andExpect(jsonPath("data.contents[*].status").exists())
                .andExpect(jsonPath("data.contents[*].sellerId").exists())
                .andExpect(jsonPath("data.contents[*].chatCount").exists())
                .andExpect(jsonPath("data.contents[*].wishCount").exists());

        // docs
        resultActions.andDo(document("items/readAll",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                requestParameters(
                        parameterWithName("cursor").description("페이징 커서"),
                        parameterWithName("categoryId").description("카테고리 아이디"),
                        parameterWithName("region").description("지역"),
                        parameterWithName("size").description("페이지 크기")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.contents[].itemId").type(NUMBER).description("상품 아이디"),
                        fieldWithPath("data.contents[].thumbnailUrl").type(STRING).description("썸네일 이미지 url"),
                        fieldWithPath("data.contents[].title").type(STRING).description("상품 제목"),
                        fieldWithPath("data.contents[].tradingRegion").type(STRING).description("거래 지역"),
                        fieldWithPath("data.contents[].createdAt").type(STRING).description("상품 등록 시간"),
                        fieldWithPath("data.contents[].price").type(NUMBER).description("상품 가격"),
                        fieldWithPath("data.contents[].status").type(STRING).description("상품 판매 상태"),
                        fieldWithPath("data.contents[].sellerId").type(STRING).description("판매자 아이디"),
                        fieldWithPath("data.contents[].chatCount").type(NUMBER).description("채팅 수"),
                        fieldWithPath("data.contents[].wishCount").type(NUMBER).description("관심 수"),
                        fieldWithPath("data.paging.nextCursor").type(NUMBER).description("다음 페이지 커서"),
                        fieldWithPath("data.paging.hasNext").type(BOOLEAN).description("다음 페이지 존재 여부")
                )
        ));
    }

    @DisplayName("상품 상세페이지 조회")
    @Test
    void read() throws Exception {
        // given
        given(itemReadFacade.read(anyLong(), anyLong()))
                .willReturn(FixtureFactory.createSellerItemDetailResponse());

        // when
        var response = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/items/{itemId}", 1)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.isSeller").exists())
                .andExpect(jsonPath("data.imageUrls").isArray())
                .andExpect(jsonPath("data.seller").exists())
                .andExpect(jsonPath("data.status").exists())
                .andExpect(jsonPath("data.title").exists())
                .andExpect(jsonPath("data.categoryName").exists())
                .andExpect(jsonPath("data.createdAt").exists())
                .andExpect(jsonPath("data.content").exists())
                .andExpect(jsonPath("data.chatCount").exists())
                .andExpect(jsonPath("data.wishCount").exists())
                .andExpect(jsonPath("data.viewCount").exists())
                .andExpect(jsonPath("data.price").exists());

        // docs
        resultActions.andDo(document("items/read",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                pathParameters(
                        parameterWithName("itemId").description("상품 아이디")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.isSeller").type(BOOLEAN).description("판매자 여부"),
                        fieldWithPath("data.imageUrls").type(ARRAY).description("상품 이미지 url들"),
                        fieldWithPath("data.seller").type(STRING).description("판매자 아이디"),
                        fieldWithPath("data.status").type(STRING).description("상품 판매 상태, 판매자인 경우에만 응답에 포함"),
                        fieldWithPath("data.title").type(STRING).description("상품 제목"),
                        fieldWithPath("data.categoryName").type(STRING).description("상품 카테고리 이름"),
                        fieldWithPath("data.createdAt").type(STRING).description("상품 등록 시간"),
                        fieldWithPath("data.content").type(STRING).description("상품 설명"),
                        fieldWithPath("data.chatCount").type(NUMBER).description("채팅 수"),
                        fieldWithPath("data.wishCount").type(NUMBER).description("관심 수"),
                        fieldWithPath("data.viewCount").type(NUMBER).description("조회 수"),
                        fieldWithPath("data.price").type(NUMBER).description("상품 가격"),
                        fieldWithPath("data.isInWishList").ignored().type(BOOLEAN).description("관심 목록에 있는지 여부, 구매자인 경우에만 응답에 포함"),
                        fieldWithPath("data.chatRoomId").ignored().type(NUMBER).description("채팅방 아이디, 구매자인 경우에만 응답에 포함")
                )
        ));
    }
}
