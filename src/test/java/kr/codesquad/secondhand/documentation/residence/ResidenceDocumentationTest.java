package kr.codesquad.secondhand.documentation.residence;

import static kr.codesquad.secondhand.documentation.support.ConstraintsHelper.withPath;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.presentation.dto.residence.ResidenceSelectRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ResidenceDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private ResidenceService residenceService;

    @DisplayName("메인 거주 지역 선택")
    @Test
    void selectResidence() throws Exception {
        // given
        ResidenceSelectRequest request = new ResidenceSelectRequest(1L);

        willDoNothing().given(residenceService).selectResidence(anyLong(), anyLong());

        // when
        var response = mockMvc.perform(put("/api/regions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        var resultActions = response.andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."));

        // docs
        resultActions.andDo(document("residence/select",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                requestFields(
                        withPath("selectedAddressId", ResidenceSelectRequest.class).description("사용자가 선택한 지역 ID")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data").ignored()
                )
        ));
    }
}
