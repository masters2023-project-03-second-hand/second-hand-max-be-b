package kr.codesquad.secondhand.documentation.auth;

import static kr.codesquad.secondhand.documentation.support.ConstraintsHelper.withPath;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyString;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import kr.codesquad.secondhand.application.auth.AuthService;
import kr.codesquad.secondhand.application.auth.TokenService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.presentation.dto.member.AddressData;
import kr.codesquad.secondhand.presentation.dto.member.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.member.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.member.SignUpRequest;
import kr.codesquad.secondhand.presentation.dto.member.UserResponse;
import kr.codesquad.secondhand.presentation.dto.token.AccessTokenResponse;
import kr.codesquad.secondhand.presentation.dto.token.AuthToken;
import kr.codesquad.secondhand.presentation.dto.token.LogoutRequest;
import kr.codesquad.secondhand.presentation.dto.token.TokenRenewRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class AuthDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenService tokenService;

    @DisplayName("로그인")
    @Test
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest("23Yong");
        LoginResponse loginResponse = new LoginResponse(
                new AuthToken("access-token", "refresh-token"),
                new UserResponse("23Yong",
                        "profileUrl",
                        List.of(new AddressData(1L, "경기도 부천시 범안동", "범안동", true))
                )
        );

        given(authService.login(any(LoginRequest.class), anyString())).willReturn(loginResponse);

        // when
        var response = mockMvc.perform(post("/api/auth/naver/login")
                .param("code", "authorization-code")
                .param("state", "state")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.jwt.accessToken").value("access-token"))
                .andExpect(jsonPath("data.jwt.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("data.user.loginId").value("23Yong"))
                .andExpect(jsonPath("data.user.profileUrl").value("profileUrl"))
                .andExpect(jsonPath("data.user.addresses[0].addressId").value(1))
                .andExpect(jsonPath("data.user.addresses[0].fullAddressName").value("경기도 부천시 범안동"))
                .andExpect(jsonPath("data.user.addresses[0].addressName").value("범안동"))
                .andExpect(jsonPath("data.user.addresses[0].isSelected").value(true));

        // docs
        resultActions.andDo(document("auth/naver/login",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParameters(
                        parameterWithName("code").description("OAuth 서버에서 받은 인가코드"),
                        parameterWithName("state").description("CSRF 공격을 방지하기 위한 상태 값")
                ),
                requestFields(
                        withPath("loginId", LoginRequest.class).description("로그인 아이디")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.jwt.accessToken").type(STRING).description("액세스 토큰"),
                        fieldWithPath("data.jwt.refreshToken").type(STRING).description("리프레시 토큰"),
                        fieldWithPath("data.user.loginId").type(STRING).description("로그인 아이디"),
                        fieldWithPath("data.user.profileUrl").type(STRING).description("프로필 이미지 URL"),
                        fieldWithPath("data.user.addresses[*].addressId").type(NUMBER).description("지역 아이디"),
                        fieldWithPath("data.user.addresses[*].fullAddressName").type(STRING).description("주소전체이름"),
                        fieldWithPath("data.user.addresses[*].addressName").type(STRING).description("읍면동 이름"),
                        fieldWithPath("data.user.addresses[0].isSelected").type(BOOLEAN).description("사용자가 선택한 거주지역")
                )
        ));
    }

    @DisplayName("회원가입")
    @Test
    void signup() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("23Yong", List.of(1L));

        willDoNothing().given(authService).signUp(any(SignUpRequest.class), anyString(), any(MultipartFile.class));

        MockMultipartFile image = new MockMultipartFile("profile", "image.png", "image/png",
                "<<png data>>".getBytes());
        MockMultipartFile signupData = new MockMultipartFile("signupData", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when
        var response = mockMvc.perform(multipart(HttpMethod.POST, "/api/auth/naver/signup")
                .file(image)
                .file(signupData)
                .param("code", "authorization-code")
                .param("state", "state")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then
        var resultActions = response
                .andExpect(status().isCreated())
                .andExpect(jsonPath("statusCode").value(201))
                .andExpect(jsonPath("message").value("성공했습니다."));

        // docs
        resultActions.andDo(document("auth/naver/signup",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestParameters(
                        parameterWithName("code").description("OAuth 서버에서 받은 인가코드"),
                        parameterWithName("state").description("CSRF 공격을 방지하기 위한 상태 값")
                ),
                requestPartFields("signupData",
                        withPath("loginId", SignUpRequest.class).description("로그인 아이디"),
                        withPath("addressIds", SignUpRequest.class).description("주소 아이디들")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data").ignored()
                )
        ));
    }

    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        // given
        willDoNothing().given(authService).logout(any(HttpServletRequest.class), anyString());

        // when
        var response = mockMvc.perform(post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":  \"39pcj0f23.3902p3.g-332\"}"));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."));

        // docs
        resultActions.andDo(document("auth/logout",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                requestFields(
                        withPath("refreshToken", LogoutRequest.class).description("리프레시 토큰")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data").ignored()
                )
        ));
    }

    @DisplayName("액세스 토큰 갱신")
    @Test
    void renewAccessToken() throws Exception {
        // given
        given(tokenService.renewAccessToken(anyString()))
                .willReturn(new AccessTokenResponse("access-token"));

        TokenRenewRequest request = new TokenRenewRequest("refresh-token");

        // when
        var response = mockMvc.perform(post("/api/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // then
        var resultActions = response
                .andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.accessToken").value("access-token"));

        // docs
        resultActions.andDo(document("auth/token",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                        withPath("refreshToken", TokenRenewRequest.class).description("리프레시 토큰")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.accessToken").type(STRING).description("갱신된 액세스 토큰")
                )
        ));
    }
}
