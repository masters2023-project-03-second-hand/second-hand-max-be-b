package kr.codesquad.secondhand.documentation.category;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import kr.codesquad.secondhand.application.category.CategoryService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.presentation.dto.category.CategoryListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

public class CategoryDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private CategoryService categoryService;

    @DisplayName("카테고리 목록 조회")
    @Test
    void readAllCategories() throws Exception {
        // given
        given(categoryService.read()).willReturn(CategoryListResponse.toResponse(
                List.of(Category.builder()
                        .id(1L)
                        .name("가전잡화")
                        .imageUrl("image-url")
                        .build())));

        // when
        var response = mockMvc.perform(request(HttpMethod.GET, "/api/categories"));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.categories[0].id").value(1))
                .andExpect(jsonPath("data.categories[0].name").value("가전잡화"))
                .andExpect(jsonPath("data.categories[0].imageUrl").value("image-url"));

        // docs
        resultActions.andDo(document("categories",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.categories[*].id").type(NUMBER).description("카테고리 아이디"),
                        fieldWithPath("data.categories[*].name").type(STRING).description("카테고리 이름"),
                        fieldWithPath("data.categories[*].imageUrl").type(STRING).description("카테고리 이미지 URL")
                )));
    }
}
