package kr.codesquad.secondhand.documentation.fcm;

import static kr.codesquad.secondhand.documentation.support.ConstraintsHelper.withPath;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kr.codesquad.secondhand.application.firebase.FcmTokenService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.presentation.dto.fcm.FcmTokenUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class FcmDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private FcmTokenService fcmTokenService;

    @DisplayName("FCM 토큰 저장")
    @Test
    void saveFcmToken() throws Exception {
        // given
        willDoNothing().given(fcmTokenService).updateToken(anyString(), anyLong());

        // when
        var response = mockMvc.perform(RestDocumentationRequestBuilders.patch("/api/fcm-token")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"token\": \"testTokenValue\"}"));
        // then
        var resultActions = response.andExpect(status().isOk());

        // docs
        resultActions
                .andDo(document("fcm/save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                        ),
                        requestFields(
                                withPath("token", FcmTokenUpdateRequest.class).description("FCM 토큰 값")
                        )
                ));

    }
}
