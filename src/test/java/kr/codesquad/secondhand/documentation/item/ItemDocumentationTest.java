package kr.codesquad.secondhand.documentation.item;

import static kr.codesquad.secondhand.documentation.support.ConstraintsHelper.withPath;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kr.codesquad.secondhand.application.item.ItemService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
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

public class ItemDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private ItemService itemService;

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
}
